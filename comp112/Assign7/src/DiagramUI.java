import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;

import ecs100.*;

public class DiagramUI {
	
	private IShape selected = null;
	private Line selceted_line = null;
	
	private List<Line> lines;
	private List<IShape> shapes;
	private boolean sticky_mode = false;
	private int width = 256 , height = 256;
	private boolean shape_changed = false;
	private boolean fast_draw = false;
	
	private Color fillcolour = Color.white;
	private Color bordercolour = Color.black;
	private Color fontcolour = Color.black;
	private Color linecolour = Color.black;
	
	private int id_counter = 0;
	private String default_text = "";
	private Point camera = new Point(0,0);
	
	/**
	 * Mouse Modes
	 *  0 - Select Shape
	 *  1 - Select Line
	 * -1 - Delete Shape
	 *-10 - Delete Line
	 *  5 - Line Select 1st Object
	 *  6 - Line Select Shape to attach to
	 * 10 - Create Rectangle
	 * 11 - Create Oval
	 * 12 - Create Hexagon
	 */
	private int mouse_mode = 0;
	
	private boolean render = true;
	
	public DiagramUI()
	{
		lines = new ArrayList<Line>();
		shapes = new ArrayList<IShape>();
		
	}
	
	public void start()
	{
		UI.initialise();
		UI.setImmediateRepaint(false);
		init();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (render)
				{
					try {
						if (UI.getGraphics() != null)
							draw();
					} catch (NullPointerException ex)
					{
						//Graphics Panel turns null during a re-draw
						//This thread sometimes get caught out
					}
					UI.sleep(16);
				}
			}
		}).start();
		
	}
	
	private boolean validSeclection ()
	{
		return (selected != null && selected.getVisability() && selected.disposed() == false);
	}
	
	private void select_shape( int x , int y)
	{
		boolean selected_one = false;
		for (IShape shape : shapes)
		{
			if ( shape.select(x, y) )
			{
				selected = shape;
				selected_one = true;
			}
		}
		if (!selected_one) {
			selected = null;
			shape_changed = true;
		} else {
			//Move the Selected to the top of the draw stack
			shapes.remove(selected);
			shapes.add(shapes.size(), selected);
			shape_changed = true;
			selceted_line = null;
		}
	}
	
	public void addShape (IShape shape)
	{
		this.shapes.add(shape);
		if ( shape.getID() > this.id_counter)
		{
			this.id_counter = shape.getID() + 1;
		}
	}
	
	public void addLine(Line line)
	{
		this.lines.add(line);
	}
	
	public List<Line> getLines()
	{
		return this.lines;
	}
	
	public List<IShape> getShapes()
	{
		return this.shapes;
	}
	
	public void Clear()
	{
		this.shapes.clear();
		this.lines.clear();
		this.id_counter = 0;
		selected = null;
	}
	
	private Line select_line( int x , int y)
	{
		for (Line line : lines)
		{
			if ( line.select(x, y) )
			{
				return line;
			}
		}
		return null;
	}
	
	//UI Rendering
	private int old_lines_length = -1;
	private int old_shapes_length = -1;
	private void draw()
	{
		if (shapes.size() != old_shapes_length || lines.size() != old_lines_length || shape_changed )
		{
			UI.clearGraphics(false);
			for (int i = 0; i < lines.size(); i++)
			{
				if (lines.get(i).draw( (int)camera.getX() , (int)camera.getY() ) == false)
				{
					lines.remove(i--);
				}
			}
			
			if (selceted_line != null && mouse_mode == 0 )
			{
				UI.setLineWidth(4.0);
				selceted_line.draw((int)camera.getX() , (int)camera.getY());
				UI.setLineWidth(1.0);
			}
			
			for (IShape shape : shapes)
			{
				if (shape.disposed() == false)
					shape.draw( (int)camera.getX() , (int)camera.getY() );
			}
			
			if ( validSeclection() && mouse_mode == 0 )
			{
				UI.setLineWidth(4.0);
				selected.draw((int)camera.getX() , (int)camera.getY());
				UI.setLineWidth(1.0);
			}
				
			draw_hud();
			
			UI.repaintGraphics();
			shape_changed = false;
		}
		if (fast_draw)
		{
			UI.repaintGraphics();
			fast_draw = false;
		}
		UI.printMessage("Mode : " + mouse_mode + " Sticky Mode : " + this.sticky_mode + " Camera Position : " + (int)camera.getX() + ", " + (int)camera.getY());
		old_lines_length = lines.size();
		old_shapes_length = shapes.size();
	}
	
	//HUD displaying Colours of Border/fill/text/line
	private void draw_hud()
	{
		UI.setColor(Color.white);
		UI.fillRect(0, 0, 355, 20);
		UI.setColor(Color.black);
		UI.drawRect(0, 0, 355, 20);
		
		UI.setColor(Color.black);
		UI.setFontSize(12);
		UI.drawString("Default fill:", 5, 15);
		UI.setColor(fillcolour);
		UI.fillRect(65, 5, 10, 10);
		
		UI.setColor(Color.black);
		UI.setFontSize(12);
		UI.drawString("Default Border:", 80, 15);
		UI.setColor(bordercolour);
		UI.fillRect(165, 5, 10, 10);
		
		UI.setColor(Color.black);
		UI.setFontSize(12);
		UI.drawString("Default Line:", 180, 15);
		UI.setColor(linecolour);
		UI.fillRect(255, 5, 10, 10);
		
		UI.setColor(Color.black);
		UI.setFontSize(12);
		UI.drawString("Default Text:", 270, 15);
		UI.setColor(fontcolour);
		UI.fillRect(340, 5, 10, 10);
	}
	
	//UI Functions
	private void init ()
	{
		//Key bindings
		UI.setKeyListener(new UIKeyListener() {
			@Override
			public void keyPerformed(String key) {
				switch (key)
				{
					case("L"):
						sticky_mode = true;
					case("l"):
					mouse_mode = 5;
					break;
					case("R"):
						sticky_mode = true;
					case("r"):
						mouse_mode = 10;
						break;
					case("O"):
						sticky_mode = true;
					case("o"):
						mouse_mode = 11;
						break;
					case("H"):
						sticky_mode = true;
					case("h"):
						mouse_mode = 12;
						break;
					case("Delete"):
					case("Backspace"):
						if ( selceted_line != null )
						{
							lines.remove(selceted_line);
							selceted_line = null;
						} else if (validSeclection())
						{
							selected.dispose();
							shapes.remove(selected);
						}
						shape_changed = true;
						break;
					case("Space"):
						sticky_mode = false;
						mouse_mode = 0;
						break;
					case("Up"):
						camera.move(camera.x , camera.y - 10 );
						shape_changed = true;
						break;
					case("Down"):
						camera.move( camera.x , camera.y + 10 );
						shape_changed = true;
						break;
					case("Left"):
						camera.move( camera.x -10 , camera.y );
						shape_changed = true;
						break;
					case("Right"):
						camera.move( camera.x + 10 , camera.y );
						shape_changed = true;
						break;
					default:
						break;
				}
			}
		});
		
		//Give the User some idea what hot keys there are
		UI.println("<==== Hotkeys ====>");
		UI.println("Key - Function");
		UI.println("r - Make a Rectangle");
		UI.println("Shift+r - Make a Rectangle (sticky mode)");
		UI.println("o - Make an Oval");
		UI.println("Shift+o - Make an Oval (sticky mode)");
		UI.println("h - Make a Hexagon");
		UI.println("shift+h - Make a Hexagon (sticky mode)");
		UI.println("l - Make a Line");
		UI.println("shift+l - Make a line (sticky mode)");
		UI.println("Arrow Keys - Move around");
		UI.println("Space - Clear current command and disable sticky mode");
		UI.println("Delete/backspace - delete selected object");
		
		//Give a UI Option for going into Selection mode
		UI.addButton("Select", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				mouse_mode = 0;
			}
		});
		
		UI.addButton("Add Line", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				mouse_mode = 5;
			}
		});
		
		//Ui Option to go into Rectangle creation mode
		UI.addButton("Add Rectangle", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				mouse_mode = 10;
			}
		});
		//Ui Option to go into Oval creation mode
		UI.addButton("Add Oval", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				mouse_mode = 11;
			}
		});
		
		//Ui Option to go into Hexagon creation mode
		UI.addButton("Add Hexagon", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				mouse_mode = 12;
			}
		});
		
		UI.addSlider("Width (px)", 1, 512, new UISliderListener() {
			@Override
			public void sliderPerformed(String name, double value) {
				width = (int)value;
				if (validSeclection()) {
					selected.setWidth((int)value);
					shape_changed = true;
				}
			}
		});
		
		UI.addSlider("Hieght (px)" , 1 , 512 , new UISliderListener() {
			@Override
			public void sliderPerformed(String name, double value) {
				height = (int)value;
				if (validSeclection()) {
					selected.setHeight((int)value);
					shape_changed = true;
				}
				
			}
		});
		
		UI.addTextField("Set Text", new UITextFieldListener() {
			@Override
			public void textFieldPerformed(String name, String newText) {
				if (validSeclection())
				{
					selected.setText(newText);
					shape_changed = true;
				} else {
					default_text = newText;
				}
				
			}
		});
		
		UI.addButton("Fill Colour", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				if (validSeclection())
				{
					selected.setFill( JColorChooser.showDialog(null, "Select Shape's Fill Colour", selected.getFill() )  );
					shape_changed = true;
				} else
					fillcolour = JColorChooser.showDialog(null, "Select Default Fill Colour", fillcolour);
			}
		});
		
		
		UI.addButton("Border Colour", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				if (validSeclection())
				{
					selected.setBorder( JColorChooser.showDialog(null, "Select Shape's Border Colour", selected.getBorder() )  );
					shape_changed = true;
				} else
					bordercolour = JColorChooser.showDialog(null, "Select Default Fill Colour", bordercolour);
			}
		});
		
		UI.addButton("Font Colour", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				if (validSeclection())
				{
					selected.getText().setColor( JColorChooser.showDialog(null, "Select Shape's Font Colour", selected.getBorder() )  );
					shape_changed = true;
				} else
					fontcolour = JColorChooser.showDialog(null, "Select Default Font Colour", fontcolour);
			}
		});
		
		UI.addButton("Line Colour", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				if (selceted_line != null)
				{
					selceted_line.setColor( JColorChooser.showDialog(null, "Select Line Colour", selceted_line.getColour()) );
					shape_changed = true;
					
				} else if (validSeclection())
				{
					Color tmp = JColorChooser.showDialog(null, "Select Line Colour for all Connected to this shape", linecolour);
					for (Line line : lines)
					{
						if (line.connectsTo(selected))
						{
							line.setColor(tmp);
						}
					}
					shape_changed = true;
				} else
					linecolour = JColorChooser.showDialog(null, "Select Default Line Colour", linecolour);
			}
		});
		
		// HACK : Terrible UI Name get a better one
		UI.addButton("Toggle Sticky Shape", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				sticky_mode = !sticky_mode;
				if (sticky_mode)
				{
					UI.println("Sticky Shape Mode is enabled");
					UI.println("The cursor will retain the shape last used");
				} else {
					UI.println("Sticky Shape Mode is disabled");
					UI.println("The cursor will NOT retain the shape last used");
					UI.println("Mouse mode is set back to selector");
					mouse_mode = 0;
				}
			}
		});
		
		UI.addButton("Delete", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				if (validSeclection())
				{
					selected.dispose();
					shapes.remove(selected);
				}else if ( selceted_line != null )
				{
					lines.remove(selceted_line);
				}
				else {
					mouse_mode = -1;
				}
			}
		});
		
		final DiagramUI _this = this;
		UI.addButton("Save", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				String save = UIFileChooser.save("Save the Diagram");
				if (save != null)
				{
					Loader.Save(save, _this );
				}
			}
		});
		
		UI.addButton("Load", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				String load = UIFileChooser.open("Open a Diagram");
				if (load != null)
				{
					Clear();
					Loader.Load(load, _this );
				}
			}
		});
		
		UI.addButton("Clear", new UIButtonListener() {
			@Override
			public void buttonPerformed(String name) {
				Clear();
			}
		});
		
		//Difference between the top left and position clicked
		final Point offset = new Point();
		final Point press_start = new Point();
		final Point camera_start = new Point();
		final Point last_mouse = new Point();
		// Mouse Controller for clicks ( shape creation )
		UI.setMouseMotionListener(new UIMouseListener() {
			@Override
			public void mousePerformed(String action, double x, double y) {
				double cam_x = x + camera.getX();
				double cam_y = y + camera.getY();
				
				//Create new Objects
				if ( action.equals("clicked") )
				{
					if (mouse_mode == 10)
					{
						shapes.add(new Rectangle( id_counter++ , (int)cam_x - width/2, (int)cam_y - height/2, width, height, bordercolour , fillcolour));
						shapes.get( shapes.size() - 1 ).getText().setColor( fontcolour );
						shapes.get( shapes.size() - 1  ).setText(default_text);
						selected = shapes.get( shapes.size() - 1 );
						if (!sticky_mode)
							mouse_mode = 0;
					}
					if (mouse_mode == 11)
					{
						shapes.add(new Oval( id_counter++ , (int)cam_x - width/2, (int)cam_y - height/2, width, height, bordercolour , fillcolour));
						shapes.get( shapes.size() - 1 ).getText().setColor( fontcolour );
						shapes.get( shapes.size() - 1  ).setText(default_text);
						selected = shapes.get( shapes.size() - 1 );
						if (!sticky_mode)
							mouse_mode = 0;
					} 
					if (mouse_mode == 12)
					{
						shapes.add(new Hexagon( id_counter++ , (int)cam_x - width/2, (int)cam_y - height/2, width, height, bordercolour , fillcolour));
						shapes.get( shapes.size() - 1 ).getText().setColor( fontcolour );
						shapes.get( shapes.size() - 1  ).setText(default_text);
						selected = shapes.get( shapes.size() - 1 );
						if (!sticky_mode)
							mouse_mode = 0;
					}
					
					if (mouse_mode == -1)
					{
						
						select_shape((int)cam_x , (int)cam_y);
						if (validSeclection()) {
							selected.dispose();
							shapes.remove(selected);
						} else {
							Line l = select_line((int)cam_x, (int)cam_y);
							if ( l != null )
							{
								lines.remove(l);
							}
						}
						if (!sticky_mode)
							mouse_mode = 0;
					}
					
				} else if (action.equals("pressed"))
				{
					if (mouse_mode == 0)
					{
						select_shape((int)cam_x , (int)cam_y);
						if (validSeclection()) {
							Point selected_pos = selected.getPosition();
							offset.setLocation( cam_x - selected_pos.getX() , cam_y - selected_pos.getY() );
						} else {
							selceted_line = select_line((int)cam_x , (int)cam_y);
						}
						press_start.setLocation(x , y);
						camera_start.setLocation(camera);
					} else if (mouse_mode == 1)
					{
						
					} else if (mouse_mode == 5)
					{
						select_shape((int)cam_x , (int)cam_y);
						if (validSeclection()) {
							mouse_mode = 6;
							last_mouse.setLocation( x , y );
							
							Point center = selected.getCenter();
							press_start.setLocation(center.getX() - camera.getX() , center.getY() - camera.getY());
							camera_start.setLocation(camera);
						}
					}
					

				} else if (action.equals("dragged") )
				{
					if (mouse_mode == 6)
					{
							//Remove last line
						UI.invertLine( press_start.getX()   ,  press_start.getY()  
									, last_mouse.getX()   , last_mouse.getY()  );
						//Draw a new line
						UI.invertLine( press_start.getX()   ,  press_start.getY()  
										, x   , y  );
						
						selected.draw( (int)camera.getX(), (int)camera.getY() );
						last_mouse.setLocation( x , y );
						fast_draw = true;
					}
				} else if (action.equals("released") )
				{
					if (mouse_mode == 6)
					{
						//Remove last line
						UI.invertLine( press_start.getX()   ,  press_start.getY()  
								, last_mouse.getX()   , last_mouse.getY()  );
						selected.draw( (int)camera.getX(), (int)camera.getY() );
						fast_draw = true;
						
						IShape old_selected = selected;
						select_shape((int)cam_x , (int)cam_y );
						
						if (validSeclection()) {
							lines.add(new Line( old_selected , selected , linecolour )  );
							selected = null;
						}
						
						if (!sticky_mode)
							mouse_mode = 0;
						else
							mouse_mode = 5;
					}
				}
				
				if (validSeclection())
				{
					if (action.equals("dragged") && selected.select( (int)cam_x, (int)cam_y) && mouse_mode == 0 )
					{
						selected.setPosition(new Point( (int)(cam_x - offset.getX())  ,
														(int)(cam_y - offset.getY()) ));
						shape_changed = true;
					}
				} else {
					if (action.equals("dragged") && mouse_mode == 0) {
						camera.move( (int)(camera_start.getX() -  (x - press_start.getX()) ) , 
									 (int)(camera_start.getY() -  (y - press_start.getY()) ) );
						shape_changed = true;
					}
				}
				
			}
		});
		
	}
	
}
