package de.el.swtTools;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public class EasyFormData {
	
	/**
	 * Creates a new ELFormData Object. <br />
	 * <strong>Every Parameter can be null.</strong> <br />
	 * The created FormData Object can be accessed using getFormData()
	 *  
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param padding
	 */
	public EasyFormData(Integer left, Integer right, Integer top, Integer bottom, Integer padding){
		if(left != null)
			formData.left = new FormAttachment(left, padding);
		if(right != null)
			formData.right = new FormAttachment(right, -padding);
		if(top != null)
			formData.top = new FormAttachment(top, padding);
		if(bottom != null)
			formData.bottom = new FormAttachment(bottom, -padding);
		this.padding = padding;
	}
	
	/**
	 * Creates a new ELFormData Object.<br />
	 * <strong>Every Parameter can be null.</strong> <br />
	 * The created FormData Object can be accessed using getFormData()
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param padding
	 * @param width
	 * @param height
	 */
	public EasyFormData(Integer left, Integer right, Integer top, Integer bottom, Integer padding, Integer width, Integer height){
		if(left != null)
			formData.left = new FormAttachment(left, padding);
		if(right != null)
			formData.right = new FormAttachment(right, -padding);
		if(top != null)
			formData.top = new FormAttachment(top, padding);
		if(bottom != null)
			formData.bottom = new FormAttachment(bottom, -padding);
		if(width != null)
			formData.width = width;
		if(height != null)
			formData.width = height;
		this.padding = padding;
	}
	
	private int padding = 0;
	private FormData formData = new FormData();
	
	/**
	 * Returns the FormData object.
	 * @return
	 */
	public FormData getFormData(){
		return formData;
	}
	
	/**
	 * Sets the control attached to the left.
	 * @param control
	 */
	public void setLeftControl(Control control){
		formData.left = new FormAttachment(control, padding);
	}

	/**
	 * Sets the control attached to the right.
	 * @param control
	 */
	public void setRightControl(Control control){
		formData.right = new FormAttachment(control, -padding);
	}
	
	/**
	 * Sets the control attached to the top.
	 * @param control
	 */
	public void setTopControl(Control control){
		formData.top = new FormAttachment(control, padding);
	}
	
	/**
	 * Sets the control attached to the bottom.
	 * @param control
	 */
	public void setBottomControl(Control control){
		formData.bottom = new FormAttachment(control, -padding);
	}

	/**
	 * Sets the width.
	 * @param width
	 */
	public void setWidth(int width){
		formData.width = width;
	}
	
	/**
	 * Sets the height.
	 * @param height
	 */
	public void setHeight(int height){
		formData.height = height;
	}
}
