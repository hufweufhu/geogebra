package geogebra.web.gui.menubar;

import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.StackPanel;

public class SideBarMenuW extends FlowPanel {

	AppW app;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private HelpMenuW helpMenu;
	private OptionsMenuW optionsMenu;
	private EditMenuW editMenu;
	private PerspectivesMenuW perspectivesMenu;
	private Runnable closeCallback;

	public SideBarMenuW(AppW app, Runnable closeCallback) {

		this.app = app;
		this.closeCallback = closeCallback;
		createFileMenu();
		createPerspectivesMenu();
		createEditMenu();
		createViewMenu();
		createOptionsMenu();
		createHelpMenu();
		
		StackPanel sp = new StackPanel();		
		sp.add(fileMenu, setHTML("File"), true);
		sp.add(editMenu, setHTML("Edit"), true);
		sp.add(perspectivesMenu, setHTML("Perspectives"), true);
		sp.add(viewMenu, setHTML("View"), true);
		sp.add(optionsMenu, setHTML("Options"), true);
		sp.add(helpMenu, setHTML("Help"), true);
		
		add(sp);
		
	}

	private String setHTML(String s){
		return  "<span style= \"font-size:80% \"  >" + s + "</span>";
	}
	
	
	
	private void createFileMenu() {
		fileMenu = new FileMenuW(app, true, closeCallback);
		//add(fileMenu);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
		//add(fileMenu);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {
		
		
		viewMenu = (app.isApplet()) ? new ViewMenuW(app)
		        : new ViewMenuApplicationW(app);
		
		//add(dp);
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenuW(app);
		//addItem(app.getMenu("Help"), helpMenu);
	}

	private void createOptionsMenu() {
		optionsMenu = new OptionsMenuW(app);
		//addItem(app.getMenu("Options"), optionsMenu);
	}

	public EditMenuW getEditMenu() {
	    return editMenu;
    }

	public void updateMenubar() {
		app.getOptionsMenu().update();
		if (!app.isApplet()) {
			((ViewMenuApplicationW) viewMenu).update();
		}
    }

	
}
