import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class KdTree {
    private class KdNode {
        private final Point2D   point;          // key for searching
        private final RectHV    rect;           // field that this point represents
        private final int       level;          // field to determine vertical or horizontal
        private KdNode          left    = null; // left child
        private KdNode          right   = null; // right child

        KdNode(Point2D p, RectHV rect, int level) {
            point = p;
            this.rect = rect;
            this.level = level;
        }
    }

    private KdNode root = null;
    private int cntOfNodes = 0;

    public KdTree() {
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return cntOfNodes;
    }

    public void insert(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException("Point can not be null");
        }
        if (contains(point)) {
            return;
        }

        RectHV rect = new RectHV(0, 0, 1, 1);
        root = insert(root, point, rect, 0);
    }

    private KdNode insert(KdNode r, Point2D point, RectHV rect, int level) {
        if (r == null) {
            ++cntOfNodes;
            return new KdNode(point, rect, level);
        }

        Comparator<Point2D> comparator = (level & 1) == 0 ? Point2D.X_ORDER : Point2D.Y_ORDER;
        int cmp = comparator.compare(point, r.point);
        ArrayList<RectHV> subDivisions = getSubDivisions(r, rect);
        if (cmp < 0) {
            r.left = insert(r.left, point, subDivisions.get(0), level + 1);
        } else {
            r.right = insert(r.right, point, subDivisions.get(1), level + 1);
        }

        subDivisions.clear();
        return r;
    }

    private ArrayList<RectHV> getSubDivisions(KdNode node, RectHV rect) {
        Point2D point = node.point;
        RectHV first, second;
        ArrayList<RectHV> subDivisions = new ArrayList<>(2);

        if ((node.level & 1) == 0) {
            // left part
            first = new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax());
            // right part
            second = new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
        } else {
            // down part
            first = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y());
            // up part
            second = new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
        }

        subDivisions.add(first);
        subDivisions.add(second);

        return subDivisions;
    }

    private KdNode get(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException("point can not be null reference");
        }

        return get(root, point);
    }

    private KdNode get(KdNode r, Point2D point) {
        if (r == null) {
            return null;
        }

        Comparator<Point2D> comparator = (r.level & 1) == 0 ? Point2D.X_ORDER : Point2D.Y_ORDER;
        int cmp = comparator.compare(point, r.point);
        if (cmp < 0) {
            return get(r.left, point);
        } else {
            if (point.compareTo(r.point) == 0) {
                return r;
            }
            return get(r.right, point);
        }
    }

    public boolean contains(Point2D point) {
        return get(point) != null;
    }

    public void draw() {
        StdDraw.setPenColor(0, 0, 0);

        Point2D topLeft = new Point2D(0, 0);
        Point2D bottomRight = new Point2D(1, 1);
        StdDraw.rectangle((topLeft.x() + bottomRight.x()) / 2, (topLeft.y() + bottomRight.y()) / 2,
                (bottomRight.x() - topLeft.x()) / 2, (bottomRight.y() - topLeft.y()) / 2);
        drawTree(root, new RectHV(topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y()));
    }

    private void drawTree(KdNode node, RectHV rect) {
        if (node == null) {
            return;
        }

        // prefix handling
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.point.x(), node.point.y());
        StdDraw.setPenRadius();

        if ((node.level & 1) == 0) {
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(node.point.x(), rect.ymin(), node.point.x(), rect.ymax());
        } else {
            StdDraw.setPenColor(0, 0, 255);
            StdDraw.line(rect.xmin(), node.point.y(), rect.xmax(), node.point.y());
        }

        if (node.left != null) {
            drawTree(node.left, node.left.rect);
        }

        if (node.right != null) {
            drawTree(node.right, node.right.rect);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("rect can not be null reference!");
        }
        return () -> new PointIterator(rect);
    }

    private class PointIterator implements Iterator<Point2D> {
        private final LinkedList<Point2D> inners = new LinkedList<>();

        public PointIterator(RectHV that) {
            Queue<KdNode> queue = new Queue<>();
            if (root != null && root.rect.intersects(that)) {
                queue.enqueue(root);
            }

            while (!queue.isEmpty()) {
                KdNode p = queue.dequeue();
                if (that.contains(p.point)) {
                    inners.add(p.point);
                }

                if (p.left != null && p.left.rect.intersects(that)) {
                    queue.enqueue(p.left);
                }

                if (p.right != null && p.right.rect.intersects(that)) {
                    queue.enqueue(p.right);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !inners.isEmpty();
        }

        @Override
        public Point2D next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements!");
            }

            return inners.removeFirst();
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point can not be null reference!");
        }

        return nearest(p, root, Double.POSITIVE_INFINITY);
    }

    private Point2D nearest(Point2D p, KdNode r, double minDistance) {
        if (r == null || r.rect.distanceSquaredTo(p) > minDistance) {
            return null;
        }

        Point2D q = r.point, target = null, candidate = null;
        double distance = q.distanceSquaredTo(p);
        if (minDistance > distance) {
            target = q;
            minDistance = distance;
        }

        if (r.left == null) {
            candidate = nearest(p, r.right, minDistance);
        } else if (r.right == null) {
            candidate = nearest(p, r.left, minDistance);
        } else {
            KdNode closerNode = r.left.rect.contains(p) ? r.left : r.right;
            KdNode otherNode = closerNode == r.left ? r.right : r.left;

            candidate = nearest(p, closerNode, minDistance);
            if (candidate != null) {
                double closerDistance = candidate.distanceSquaredTo(p);
                minDistance = closerDistance;
                target = candidate;
            }

            candidate = nearest(p, otherNode, minDistance);
        }

        if (candidate != null && candidate.distanceSquaredTo(p) < minDistance) {
            target = candidate;
            minDistance = candidate.distanceSquaredTo(p);
        }

        return target;
    }

    public static void main(String[] args) {
        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(0.75, 1.0));
        kdtree.insert(new Point2D(0.0, 0.25));
        kdtree.insert(new Point2D(0.25, 0.25));
        kdtree.insert(new Point2D(0.5, 0.75));
        kdtree.insert(new Point2D(0.75, 0.0));
        kdtree.insert(new Point2D(0.0, 1.0));
        kdtree.insert(new Point2D(0.25, 0.75));
        kdtree.insert(new Point2D(1.0, 0.5));
        kdtree.insert(new Point2D(1.0, 0.25));
        kdtree.insert(new Point2D(0.0, 0.75));

        System.out.println(kdtree.contains(new Point2D(0.0, 0.75)));
    }
}
