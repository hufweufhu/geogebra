package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.main.AppW;

/**
 * upload an image directly without dialog
 * 
 * @author Alicia
 *
 */
public class UploadImageWithoutDialog {

	private UploadImagePanel uploadImagePanel;
	private AppW app;
	/**
	 * @param app
	 *            application
	 */
	public UploadImageWithoutDialog(AppW app) {
		this.app = app;
		initGUI();
	}

	private void initGUI() {
		app.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		uploadImagePanel = new UploadImagePanel(this);
		uploadImagePanel.openFileBrowserDirectly();
	}

	/**
	 * insert image after selection
	 */
	public void insertImage() {
		String data = uploadImagePanel.getImageDataURL();
		String name = uploadImagePanel.getFileName();
		app.imageDropHappened(name, data, "");
	}
}
