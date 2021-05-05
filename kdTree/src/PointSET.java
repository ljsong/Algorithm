import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class PointSET {
    private final SET<Point2D> points = new SET<>();
    public PointSET() {
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point can not be null reference!");
        }

        if (contains(p)) {
            return;
        }

        points.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point can not be null reference!");
        }
        return points.contains(p);
    }

    public void draw() {
        for (var p: points) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("rect can not be null reference!");
        }
        return () -> new PointIterator(rect);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("point can not be null reference");
        }

        Point2D nearest = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (var q: points) {
            double eachDistance = q.distanceSquaredTo(p);
            if (eachDistance < minDistance) {
                minDistance = eachDistance;
                nearest = q;
            }
        }

        return nearest;
    }

    private class PointIterator implements Iterator<Point2D> {
        private final LinkedList<Point2D> inners = new LinkedList<>();
        public PointIterator(RectHV rect) {
            for (var point: points) {
                if (rect.contains(point)) {
                    inners.add(point);
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
                throw new NoSuchElementException("No more element to fetch!");
            }

            return inners.removeFirst();
        }
    }

    public static void main(String[] args) {
        // unit testing method
    }

}
