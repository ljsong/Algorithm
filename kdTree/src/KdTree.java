import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class KdTree {
    private class KdNode implements Comparable<KdNode>{
        private Point2D     point;          // key for searching
        private RectHV      rect    = null; // field that this point represents
        private int         level;          // field to determine vertical or horizontal
        private KdNode      left    = null; // left child
        private KdNode      right   = null; // right child

        KdNode(Point2D p, int l) {
            point = p;
            level = l;
        }

        @Override
        public int compareTo(KdNode o) {
            Comparator<Point2D> comparator = (level & 1) == 0 ? Point2D.X_ORDER : Point2D.Y_ORDER;
            return comparator.compare(point, o.point);
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
        root = insert(root, point, 0);
    }

    private KdNode insert(KdNode r, Point2D point, int level) {
        if (r == null) {
            ++cntOfNodes;
            return new KdNode(point, level);
        }

        Comparator<Point2D> comparator = (level & 1) == 0 ? Point2D.X_ORDER : Point2D.Y_ORDER;
        int cmp = comparator.compare(point, r.point);
        if (cmp < 0) {
            r.left = insert(r.left, point, level + 1);
        } else if (cmp > 0) {
            r.right = insert(r.right, point, level + 1);
        }

        return r;
    }

    private ArrayList<RectHV> getSubDivisions(KdNode node, RectHV rect) {
        Point2D point = node.point;
        RectHV first = null, second = null;
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

    public KdNode get(Point2D point) {
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
        } else if (cmp > 0) {
            return get(r.right, point);
        } else {
            return r;
        }
    }

    public Point2D min() {
        KdNode p = root;
        while(p != null && p.left != null) {
            p = p.left;
        }

        return p.point;
    }

    public Point2D max() {
        KdNode p = root;
        while(p != null && p.right != null) {
            p = p.right;
        }

        return p.point;
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

        if((node.level & 1) == 0) {
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(node.point.x(), rect.ymin(), node.point.x(), rect.ymax());
        } else {
            StdDraw.setPenColor(0, 0, 255);
            StdDraw.line(rect.xmin(), node.point.y(), rect.xmax(), node.point.y());
        }

        ArrayList<RectHV> subDivisions = getSubDivisions(node, rect);
        if (node.left != null) {
            node.left.rect = subDivisions.get(0);
            drawTree(node.left, node.left.rect);
        }

        if (node.right != null) {
            node.right.rect = subDivisions.get(1);
            drawTree(node.right, node.right.rect);
        }
        subDivisions.clear();
    }

    public Iterable<Point2D> range(RectHV rect) {
        return () -> new PointIterator(rect);
    }

    private class PointIterator implements Iterator<Point2D> {
        private final LinkedList<Point2D> inners = new LinkedList<>();

        public PointIterator(RectHV that) {
            Queue<KdNode> queue = new Queue<>();
            if(root != null && root.rect.intersects(that)) {
                queue.enqueue(root);
            }

            while(!queue.isEmpty()) {
                KdNode p = queue.dequeue();
                inners.add(p.point);

                if (p.left != null && p.left.rect.intersects(that)) {
                    queue.enqueue(p.left);
                }

                if (p.right != null & p.right.rect.intersects(that)) {
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
            return inners.removeFirst();
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point can not be null reference!");
        }

        return nearest(p, root);
    }

    private Point2D nearest(Point2D p, KdNode r) {
        Point2D nearest = null;
        double maxDistance = Double.NEGATIVE_INFINITY;

        Point2D q = r.point;
        double distance = q.distanceTo(p);
        if (maxDistance > distance) {
            nearest = q;
            maxDistance = distance;
        }

        if (r.left != null) {
            double leftDistance = p.distanceTo(r.left.point);
            if (leftDistance <= maxDistance) {
                nearest(p, r.left);
            }
        }

        if (r.right != null) {
            double rightDistance = p.distanceTo(r.right.point);
            if (rightDistance <= maxDistance) {
                nearest(p, r.right);
            }
        }

        return nearest;
    }
}
