package com.jeecms.cms.entity.main.base;

import java.io.Serializable;

/**
 * This is an object that contains data related to the jc_content_product table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content_product"
 */

public abstract class BaseContentProduct implements Serializable{

	private static final long serialVersionUID = 7965823950023635689L;
	
	public static String REF = "ContentProduct";
	public static String PROP_PRODUCT_CODES = "productCodes";
	
	// constructors
		public BaseContentProduct () {
			initialize();
		}

		/**
		 * Constructor for primary key
		 */
		public BaseContentProduct (java.lang.Integer id) {
			this.setId(id);
			initialize();
		}

		/**
		 * Constructor for required fields
		 */
		public BaseContentProduct (
			java.lang.Integer id,
			java.lang.String productCodes) {

			this.setId(id);
			this.setProductCodes(productCodes);
			initialize();
		}

		protected void initialize () {}



		private int hashCode = Integer.MIN_VALUE;

		// primary key
		private java.lang.Integer id;
	
		// fields
		private java.lang.String productCodes;
		
		// one to one
		private com.jeecms.cms.entity.main.Content content;
		/**
		 * Return the unique identifier of this class
	     * @hibernate.id
	     *  generator-class="foreign"
	     *  column="content_id"
	     */
		public java.lang.Integer getId () {
			return id;
		}

		/**
		 * Set the unique identifier of this class
		 * @param id the new ID
		 */
		public void setId (java.lang.Integer id) {
			this.id = id;
			this.hashCode = Integer.MIN_VALUE;
		}
		
		/**
		 * Return the value associated with the column: productCode
		 */
		public java.lang.String getProductCodes () {
			return productCodes;
		}

		/**
		 * Set the value related to the column: productCode
		 * @param title the title value
		 */
		public void setProductCodes (java.lang.String productCodes) {
			this.productCodes = productCodes;
		}
		
		/**
		 * Return the value associated with the column: content
		 */
		public com.jeecms.cms.entity.main.Content getContent () {
			return content;
		}

		/**
		 * Set the value related to the column: content
		 * @param content the content value
		 */
		public void setContent (com.jeecms.cms.entity.main.Content content) {
			this.content = content;
		}
		
		public boolean equals (Object obj) {
			if (null == obj) return false;
			if (!(obj instanceof com.jeecms.cms.entity.main.ContentProduct)) return false;
			else {
				com.jeecms.cms.entity.main.ContentProduct contentProduct = (com.jeecms.cms.entity.main.ContentProduct) obj;
				if (null == this.getId() || null == contentProduct.getId()) return false;
				else return (this.getId().equals(contentProduct.getId()));
			}
		}

		public int hashCode () {
			if (Integer.MIN_VALUE == this.hashCode) {
				if (null == this.getId()) return super.hashCode();
				else {
					String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
					this.hashCode = hashStr.hashCode();
				}
			}
			return this.hashCode;
		}


		public String toString () {
			return super.toString();
		}
}
