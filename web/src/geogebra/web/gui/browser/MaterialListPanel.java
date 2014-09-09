package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * 
 */
public class MaterialListPanel extends FlowPanel implements ResizeListener {
	
	protected AppW app;
	/**
	 * last selected {@link MaterialListElement material}
	 */
	protected MaterialListElement lastSelected;
	/**
	 * list of all shown {@link MaterialListElement materials}
	 */
	protected List<MaterialListElement> materials = new ArrayList<MaterialListElement>();
	private MaterialCallback rc;
	private boolean personalMaterialsLoaded = false;
	
	public MaterialListPanel(final AppW app) {
		this.app = app;
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.setStyleName("materialListPanel");
		this.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(final ClickEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ClickEvent.getType());
		
		this.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(final ScrollEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ScrollEvent.getType());
		
		this.addDomHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(final TouchMoveEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, TouchMoveEvent.getType());
		rc = new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				onSearchResults(parseResponse);
			}
		};
	}

	/**
	 * sets all {@link MaterialListElement materials} to the
	 * default style (not selected, not disabled)
	 */
	public void setDefaultStyle() {
		this.lastSelected = null;
		for (final MaterialListElement mat : this.materials) {
			mat.setDefaultStyle();
		}
	}
	
	/**
	 * clears the panel and adds the local/own files and the
	 * featured materials from ggt to the panel
	 */
    public void loadFeatured() {
		clearMaterials();
		loadLocal();
		loadggt();
	}

	private void loadLocal() {
		app.getFileManager().getAllFiles();
	}
	
	protected void loadggt() {
		final GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		api.getFeaturedMaterials(new MaterialCallback() {
			
			@Override
			public void onLoaded(final List<Material> response) {
				onSearchResults(response);
				if(app.getLoginOperation().isLoggedIn()){
					MaterialListPanel.this.setLoggedIn(true);
				}
			}
		});
	}

	/**
	 * adds the new materials (matList) - GeoGebraTube only
	 * @param matList List<Material>
	 */
	public final void onSearchResults(final List<Material> matList) {
		for (int i = matList.size()-1;i>=0;i--) {
			addMaterial(matList.get(i), false);
		}
	}

	/**
	 * adds the given material to the list of {@link MaterialListElement materials} and the preview-panel
	 * The actual creation happens in LAF as it needs to be different for phone / tablet / web / widgets
	 * @param mat {@link Material}
	 * @param isLocal boolean
	 */
	public final void addMaterial(final Material mat, final boolean isLocal) {
		final MaterialListElement preview = ((GLookAndFeel)app.getLAF()).getMaterialElement(mat, this.app, isLocal);
		this.materials.add(preview);
		this.insert(preview,0);
	}

	/**
	 * clears the list of existing {@link MaterialListElement materials} and the {@link MaterialListPanel preview-panel}
	 */
	public void clearMaterials() {
		this.personalMaterialsLoaded = false;
		this.materials.clear();
		this.clear();
	}
	
	/**
	 * @return {@link MaterialListElement} last selected material
	 */
	public MaterialListElement getChosenMaterial() {
		return this.lastSelected;
	}
	
	/**
	 * sets all materials to disabled
	 */
	public void disableMaterials() {
	    for (final MaterialListElement mat : this.materials) {
	    	mat.disableMaterial();
	    }
    }

	/**
	 * @param materialElement 
	 */
	public void rememberSelected(final MaterialListElement materialElement) {
		this.lastSelected = materialElement;
	}
	
	public void displaySearchResults(final String query) {
		clearMaterials();
		if (query.equals("")) {
			loadFeatured();
			return;
		}
		searchLocal(query);
		searchGgt(query);
	}

	private void searchLocal(final String query) {
		this.app.getFileManager().search(query);
	}
	
	/**
	 * search GeoGebraTube
	 * @param query
	 */
	protected void searchGgt(final String query) {
		((GeoGebraTubeAPIW) this.app.getLoginOperation().getGeoGebraTubeAPI()).search(
				query, new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
						// FIXME implement Error Handling!
						exception.printStackTrace();
						App.debug(exception.getMessage());
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
					}
				});
    }

	/**
	 * removes the given material from the list of {@link MaterialListElement materials} and the preview-panel
	 * @param mat {@link Material}
	 */
	public void removeMaterial(final Material mat) {
		for(final MaterialListElement matElem : this.materials) {
			if (matElem.getMaterial().equals(mat)) {
				this.materials.remove(matElem);
				this.remove(matElem);
				return;
			}
		}
	}
		
	/**
	 * 
	 */
	public void setLabels() {
		for (final MaterialListElement e : this.materials) {
			e.setLabels();
		}
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}

	public void setLoggedIn(boolean b) {
	    if(b){
	    	if(!this.personalMaterialsLoaded){
	    		this.personalMaterialsLoaded = true;
	    		((GeoGebraTubeAPIW)app.getLoginOperation().getGeoGebraTubeAPI()).getUsersMaterials(app.getLoginOperation().getModel().getUserId(), rc);
	    	}
	    }else{
	    	this.loadFeatured();
	    }
	    
    }

	public void refreshMaterial(Material material, boolean isLocal) {
		
		if (!isLocal) {
			material.setSyncStamp(material.getModified());
		}
		material.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
		
		for(final MaterialListElement matElem : this.materials) {
			if (matElem.getMaterial().getId() == material.getId()) {
				matElem.setMaterial(material);
				return;
			}
		}
		
		//if material wasn't found, create new MaterialListElement
		addMaterial(material, isLocal);
    }
}
