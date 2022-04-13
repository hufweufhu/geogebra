package org.geogebra.web.full.gui.util;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.Dom;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.impl.FocusImpl;

public class ToggleButton extends FocusWidget {
	static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();
	private boolean isSelected;
	private boolean isEnabled;
	private ResourcePrototype svgUp;
	private ResourcePrototype svgDown;
	private Label label;
	private NoDragImage btnImage;

	/**
	 * constructor
	 */
	public ToggleButton() {
		setElement(DOM.createButton());
		addStyleName("button");
	}

	/**
	 * @param svgRes - image
	 */
	public ToggleButton(ResourcePrototype svgRes) {
		this();
		svgUp = svgRes;
		addStyleName("MyToggleButton");
		setSelected(false);
		setIcon(svgRes);
	}

	/**
	 * text based toggle button
	 * @param labelStr - label
	 */
	public ToggleButton(String labelStr) {
		this();
		addStyleName("MyToggleButton");
		setSelected(false);
		label = new Label(labelStr);
		getElement().appendChild(label.getElement());
	}

	/**
	 * toggle button with two faces
	 * @param svgUp - up image
	 * @param svgDown - down image
	 */
	public ToggleButton(ResourcePrototype svgUp, ResourcePrototype svgDown) {
		this(svgUp);
		this.svgDown = svgDown;
	}

	/**
	 * @param image - resource
	 */
	public void setIcon(final ResourcePrototype image) {
		this.getElement().removeAllChildren();
		if (image != null) {
			btnImage = new NoDragImage(image, 24, 24);
			btnImage.getElement().setTabIndex(-1);
			getElement().appendChild(btnImage.getElement());
			btnImage.setPresentation();
		}
	}

	public void setText(String labelStr) {
		label.setText(labelStr);
	}

	/**
	 * set selected state and style
	 * @param isSelected - true if selected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (svgDown == null) {
			Dom.toggleClass(this, "selected", isSelected);
		}
	}

	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param handler - click handler
	 */
	public void addFastClickHandler(FastClickHandler handler) {
		Dom.addEventListener(this.getElement(), "click", (e) -> {
			setSelected(!isSelected);
			if (svgDown != null) {
				setIcon(isSelected ? svgDown : svgUp);
			}
			handler.onClick(this);
			e.stopPropagation();
		});
	}

	/**
	 * Button instances override this method to update the state of the button
	 * (e.g. visibility) based on a given array of GeoElements.
	 *
	 * @param geos
	 *            List of active GeoElements
	 */
	public void update(List<GeoElement> geos) {
		// do nothing
	}

	@Override
	public void setTabIndex(int index) {
		focusImpl.setTabIndex(getElement(), index);
	}

	/**
	 * @param imageAltText - alt text for image
	 */
	public void setImageAltText(String imageAltText) {
		btnImage.setAltText(imageAltText);
	}

	@Override
	public void setFocus(boolean focused) {
		if (focused) {
			focusImpl.focus(getElement());
		} else {
			focusImpl.blur(getElement());
		}
	}
}
