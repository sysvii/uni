import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Road implements IDrawable {

    int id;
    int type;
    String label;
    String city;
    boolean isOneWay;
    byte speedLimit;
    // TODO: Make enum for Road Class
    byte roadClass;
    double length;

    Set<RoadUsers> roadUsers;

    Color colour;

    private Set<RoadSegment> roadSegments;

    public Road(int ID, int Type, String Label, String City, boolean IsOneWay, byte SpeedLimit, byte RoadClass,
                Set<RoadUsers> roadUsers) {
        this.id = ID;
        this.type = Type;

        this.label = Label;
        this.city = City;
        this.isOneWay = IsOneWay;
        this.speedLimit = SpeedLimit;
        this.roadClass = RoadClass;
        this.roadUsers = roadUsers;

        this.length = 0.0;
        this.roadSegments = new HashSet<>();
        colour = Color.black;
    }

    /**
     * @see super.draw
     */
    public void draw(Graphics g, Location originOffset, double scale) {
        if (this.roadSegments == null) return;

        g.setColor(colour);
        for(RoadSegment seg : this.roadSegments) {
            seg.draw(g, originOffset, scale);
        }
    }

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Road) {
            Road rd = (Road)(ob);
            return (rd.id == this.id);
        }
        return false;
    }

    /**
     * @see super.getArea
     */
    @Override
    public Rectangle getArea() {
        return null;
    }

    /**
     * Load Roads from tabulated file
     * @param Roads file to be loaded from
     * @param RoadTrie Trie of Road names to be filled
     * @param RoadLabel a map of road names to list of roads with that name
     * @return A map of Road ID's to Roads
     */
    public static java.util.Map<Integer, Road> LoadFromFile(File Roads, TrieNode RoadTrie, Map<String, List<Road>> RoadLabel) {
        TreeMap<Integer, Road> roads = new TreeMap<>();
        assert (Roads.isFile());
        assert (Roads.canRead());

        try {
            BufferedReader roadsReader = new BufferedReader(new FileReader(Roads));

            roadsReader.readLine(); // Skip the header line

            String line;
            while ((line = roadsReader.readLine()) != null) {
                Queue<String> data = new ArrayDeque<>(java.util.Arrays.asList(line.split("\t")));
                int id = Integer.parseInt(data.poll());
                int type = Integer.parseInt(data.poll());
                String label = data.poll().trim();
                RoadTrie.insert(label);
                String city = data.poll().trim();
                boolean oneway = Integer.parseInt(data.poll()) == 1;
                byte speed = Byte.parseByte(data.poll());
                byte roadclass = Byte.parseByte(data.poll());
                Set<RoadUsers> users = new HashSet<>();

                if (Integer.parseInt(data.poll()) == 0) {
                    users.add(RoadUsers.ALLOW_CARS);
                }
                if (Integer.parseInt(data.poll()) == 0) {
                    users.add(RoadUsers.ALLOW_PEDESTRIANS);
                }
                if (Integer.parseInt(data.poll()) == 0) {
                    users.add(RoadUsers.ALLOW_CYCLISTS);
                }

                roads.put(id, new Road(id, type, label, city, oneway, speed, roadclass, users));
                if (!RoadLabel.containsKey(label)) {
                    RoadLabel.put(label, new ArrayList<Road>());
                }
                RoadLabel.get(label).add(roads.get(id));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Could not find " + Roads.getName() +
                    "\n" + e.toString());
            return null;
        } catch (IOException e) {
            System.out.println("IO Exception while operating on " + Roads.getName() +
                    "\n" + e.toString());
            return null;
        }

        return roads;
    }
    // Getters and Setters
    public Set<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public void setRoadSegments(Set<RoadSegment> roadSegments) {
        this.roadSegments = roadSegments;
    }
    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getCity() {
        return city;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public byte getSpeedLimit() {
        return speedLimit;
    }

    public byte getRoadClass() {
        return roadClass;
    }

    public Set<RoadUsers> getUsers() {
        return roadUsers;
    }

    public double getLength() {
        return length;
    }

    public double getRoadClassLimit() {
        switch (this.roadClass) {
            case 0:
                return 50.0;
            case 1:
                return 60.0;
            case 2:
                return 80.0;
            case 3:
                return 90.0;
            case 4:
                return 110.0;
            default:
                throw new RuntimeException("Unsupported case");
        }
    }

    public double getSpeed() {
        switch (this.speedLimit) {
            case(0):
                return 5.0;
            case(1):
                return 20.0;
            case(2):
                return 40.0;
            case(3):
                return 60.0;
            case(4):
                return 80.0;
            case(5):
                return 100.0;
            case(6):
                return 110.0;
            case(7):
                return 150.0; // "unlimited"
            default:
                throw new RuntimeException();
        }
    }
    @Override
    public int hashCode() {
        return id;
    }
}
