import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuadTree<T extends IDrawable> {
    private static final int SPLIT_NUMBER = 10;

    private final QuadTree<T> parent;
    private final Rectangle area;
    private int size;
    List<T> items;
    QuadTree<T>[] children;


    public QuadTree() {
        parent = null;
        children = null;
        area = new Rectangle(Integer.MIN_VALUE/2, Integer.MIN_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
        items = new ArrayList<>();
        size = 0;
    }

    public QuadTree(Rectangle Area) {
        parent = null;
        children = null;
        area = Area;
        items = new ArrayList<>();
        size = 0;
    }

    private QuadTree(Rectangle Area, QuadTree<T> Parent) {
        parent = Parent;
        children = null;
        area = Area;
        items = new ArrayList<>();
        size = 0;
    }

    public int size() {
        return this.size;
    }


    public boolean isEmpty() {
        if (items.size() != 0) {
            return true;
        }
        for (QuadTree<T> child : children) {
            if (child.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add element to the quadtree
     *
     * @param t element to be add
     * @return true if successfully added, otherwise false
     */
    public boolean add(T t) {
        if (!this.area.getBounds().contains(t.getArea())) {
            return false;
        }

        if (items.size() <= SPLIT_NUMBER) {
            size++;
            return items.add(t);
        }  else {
            if (children == null){
                split();
            }
            for (QuadTree<T> child : children) {
                if (child.getArea().contains(t.getArea())) {
                    if(child.add(t)) {
                        size++;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            size++;
            return items.add(t);
        }
    }

    /**
     * Add a whole collection to the Quadtree
     *
     * @param c collection to be added
     * @return true if successfully added, otherwise false
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean success = true;
        for (T i : c) {
            success = success && this.add(i);
        }
        return success;
    }

    /**
     * Get elements that exist in the given area
     * @param Area to check for elements
     * @return list of elements in area
     */
    public List<T> get(Rectangle Area) {
        List<T> toReturn = new ArrayList<>();

        for (T i : this.items) {
            if (i.getArea().getBounds().intersects(Area)) {
                toReturn.add(i);
            }
        }
        if (children != null) {
            for (QuadTree child : children) {
                if (child.getArea().getBounds().intersects(Area)) {
                    toReturn.addAll(child.get(Area));
                }
            }
        }
        return toReturn;
    }

    /**
     * Get the area that this QuadTree covers
     * @return
     */
    public Shape getArea() {
        return area;
    }

    /**
     * Split up elements into children
     */
    private void split() {
        if (children == null) {
            children = new QuadTree[4];

            Rectangle bounds = this.area.getBounds();
            Rectangle topLeft = new Rectangle(bounds.x, bounds.y,
                    (int)Math.ceil(bounds.width / 2.0), (int)Math.floor(bounds.height / 2.0));
            children[0] = new QuadTree<>(topLeft, this);

            Rectangle topRight = new Rectangle(bounds.x + topLeft.width, bounds.y,
                    bounds.width - topLeft.width, topLeft.height);
            children[1] = new QuadTree<>(topRight, this);

            Rectangle botLeft = new Rectangle(bounds.x, bounds.y + topLeft.height,
                    topLeft.width, bounds.height - topLeft.height);
            children[2] = new QuadTree<>(botLeft, this);

            Rectangle botRight = new Rectangle(bounds.x + botLeft.width, bounds.y + topRight.height,
                    bounds.width - botLeft.width, bounds.height - topRight.height);
            children[3] = new QuadTree<>(botRight, this);
        }

        List<T> toRemove = new ArrayList<>();
        for (T i : items) {
            for (QuadTree<T> child : children) {
                if (child.add(i)) {
                    toRemove.add(i);
                    break;
                }
            }
        }
        items.removeAll(toRemove);
    }

    /**
     * For debug purposes, render the QuadTree
     *
     * @param g
     * @param scale
     * @param offset
     */
    public void draw(Graphics g, double scale, Point offset) {
        g.setColor(Color.pink);
        g.drawRect((int) ((area.getBounds().x - offset.x) * scale),
                   (int) ((area.getBounds().y - offset.y) * scale),
                   (int) (area.getBounds().width * scale), (int) (area.getBounds().height * scale));
        if (children != null) {
            for (QuadTree<T> child : children) {
                child.draw(g, scale, offset);
            }
        }
    }

    @Override
    public String toString() {
        return "QuadTree{" +
                "items=" + items +
                ", parent=" + parent +
                ", children=" + (children != null ? children.length : "null") +
                ", area=" + area +
                '}';
    }
}
