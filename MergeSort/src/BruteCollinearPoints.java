import edu.princeton.cs.algs4.Merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BruteCollinearPoints {
    private Point[] points = null;
    private Point[] copy = null;
    private LineSegment[] segs = null;

    public BruteCollinearPoints(Point[] points) {
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
    }

    public int numberOfSegments() {
        if (segs == null) {
            return 0;
        }
        return segs.length;
    }

    public LineSegment[] segments() {
        int cntOfCollinear = 0;
        ArrayList<LineSegment> list = new ArrayList<>();
        for (int ix = 0; ix < copy.length; ++ix) {
            Merge.sort(copy);
            Point first = copy[ix];
            Comparator<Point> comparator = first.slopeOrder();
            // sort those points by their slopes compared to first point
            Arrays.sort(copy, ix, copy.length, first.slopeOrder());
            // calculate these slopes for later sorting and finding collinear

            for (int jx = ix; jx < copy.length - 1; ++jx) {
                if (comparator.compare(copy[jx], copy[jx + 1]) != 0) {
                    cntOfCollinear = 0;
                    continue;
                }
                cntOfCollinear++;
                if (cntOfCollinear == 2) {
                    list.add(new LineSegment(first, copy[jx + 1]));
                }
            }
        }

        segs = list.toArray(new LineSegment[list.size()]);
        return segs;
    }
}
