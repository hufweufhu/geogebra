package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManager;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.SaveCallback;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.gui.browser.BrowseGUI;

import com.google.gwt.storage.client.Storage;

public class FileManagerW extends FileManager {
	private static final String FILE_PREFIX = "file#";
	Storage stockStore = Storage.getLocalStorageIfSupported();
	
	public FileManagerW(AppW app) {
		super(app);
	}

	@Override
    public void delete(Material mat) {
		this.stockStore.removeItem(FILE_PREFIX + mat.getTitle());
		removeFile(mat);
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).setMaterialsDefaultStyle();
    }
	
	@Override
    public void openMaterial(Material material) {
		try {
			final String base64 = material.getBase64();
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			app.showError("LoadFileFailed");
			t.printStackTrace();
		}
    }

	@Override
    public void saveFile(final SaveCallback cb) {
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final String fileName = app.getKernel().getConstruction().getTitle();
				
				final Material mat = new Material(0, MaterialType.ggb);
				
				//TODO check if we need to set timestamp / modified
				mat.setModified(System.currentTimeMillis() / 1000);
				
				if (app.getUniqueId() != null) {
					mat.setId(Integer.parseInt(app.getUniqueId()));
					mat.setSyncStamp(app.getSyncStamp());
				}
				
				mat.setBase64(s);
				mat.setTitle(app.getKernel().getConstruction().getTitle());
				mat.setDescription(app.getKernel().getConstruction().getWorksheetText(0));
				mat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
				mat.setAuthor(app.getLoginOperation().getUserName());

				//TODO use another key
				stockStore.setItem(FILE_PREFIX + fileName, mat.toJson().toString());

				cb.onSaved();
			}
		};

		app.getGgbApi().getBase64(true, base64saver);
    }

	@Override
    public void removeFile(Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeMaterial(material);
    }

	@Override
    public void addFile(Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).addMaterial(material);
    }
	
	@Override
    protected void getFiles(final MaterialFilter filter) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				Material mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(key.substring(FILE_PREFIX.length()));
				}
				if (filter.check(mat)) {
					addFile(mat);
				}
			}
		}
	}
	
	
	@Override
	public void uploadUsersMaterials() {		
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}
		
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				Material mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
				if (mat.getAuthor().equals(this.app.getLoginOperation().getUserName())) {
					if (mat.getId() == 0) {
						upload(mat);
					} else {
						sync(mat);
					}
				}
			}
		}	
	}
}
