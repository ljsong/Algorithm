import edu.princeton.cs.algs4.Merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {

    private Point[] points = null;
    private Point[] copy = null;
    private LineSegment[] segs = null;

    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Array can't be null");
        }

        this.points = points;
        for (int ix = 0; ix < points.length; ++ix) {
            if (points[ix] == null) {
                throw new IllegalArgumentException("Item in point array can't be null");
            }
        }

        copy = points.clone();
        Merge.sort(copy);
        for (int ix = 0; ix < copy.length; ++ix) {
            if (ix > 0 && copy[ix].compareTo(copy[ix - 1]) == 0) {
                throw new IllegalArgumentException("Item in point array can't be repeated");
            }
        }
        segs = new LineSegment[copy.length];
    }

    public int numberOfSegments() {
        if (segs == null) {
            return 0;
        }
        return segs.length;
    }

    private boolean hasParent(int index, Point endPoint) {
        Point first = copy[index];
        Comparator<Point> comparator = first.slopeOrder();

        for (int ix = index - 1; ix >= 0; --ix) {
            if (comparator.compare(copy[ix], endPoint) == 0) {
                return true;
            }
        }

        return false;
    }

    public LineSegment[] segments() {
        ArrayList<LineSegment> list = new ArrayList<>();

        for (int ix = 0; ix < copy.length; ++ix) {
            Point first = copy[ix];
            int cntOfCollinear = 0;
            Comparator<Point> comparator = first.slopeOrder();
            // sort those points by their slopes compared to first point
            Arrays.sort(copy, ix, copy.length, comparator);

            for (int jx = ix + 1; jx < copy.length; ++jx) {
                if (comparator.compare(copy[jx], copy[jx - 1]) != 0) {
                    // it means we have at least 4 points are collinear when `cnt_of_collinear` >= 2
                    // we also need to check whether we have a point in front of current point
                    if (cntOfCollinear >= 2 && !hasParent(ix, copy[jx - 1])) {
                        list.add(new LineSegment(first, copy[jx - 1]));
                    }
                    cntOfCollinear = 0;
                    continue;
                }

                cntOfCollinear++;
            }

            if (cntOfCollinear >= 2 && !hasParent(ix, copy[copy.length - 1])) {
                list.add(new LineSegment(first, copy[copy.length - 1]));
            }

            Merge.sort(copy);
        }

        segs = list.toArray(new LineSegment[list.size()]);
        return segs;
    }
}
