package org.gotti.wurmunlimited.modsupport.bml;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BmlBuilder {
	
	/**
	 * Root node to work on.
	 */
	private final BmlNodeBuilder root = BmlNodeBuilder.builder("");
	
	/**
	 * private Constructor
	 */
	private BmlBuilder() {
	}
	
	/**
	 * Create an empty builder
	 * @return
	 */
	public static BmlBuilder builder() {
		return new BmlBuilder();
	}
	
	/**
	 * Wrap the contents in the default dialog structure (header, scrollable content in varray)
	 * @param title title
	 * @param horizontalScroll enable horizontal scrolling
	 * @param verticalScroll enable vertical scrolling
	 * @param rescale vertical rescale
	 */
	public BmlBuilder wrapAsDialog(String title, boolean horizontalScroll, boolean verticalScroll, boolean rescale) {
		final BmlNodeBuilder border = border(
				center(text(title, TextStyle.BOLD)),
				null,
				scroll(horizontalScroll, verticalScroll, varray(rescale).withNodes(this)),
				null,
				null);
		return builder().withNode(border);
	}
	
	/**
	 * Border layout
	 * @param north component
	 * @param west component
	 * @param center component
	 * @param east component
	 * @param south component
	 */
	public static BmlNodeBuilder border(BmlNodeBuilder north, BmlNodeBuilder west, BmlNodeBuilder center, BmlNodeBuilder east, BmlNodeBuilder south) {
		return BmlNodeBuilder.builder("border")
				.withNode(north)
				.withNode(west)
				.withNode(center)
				.withNode(east)
				.withNode(south);
	}

	/**
	 * Label
	 * @param text displayed text
	 */
	public static BmlNodeBuilder label(String text) {
		return BmlNodeBuilder.builder("label").withAttribute("text", text);
	}
	
	/**
	 * Label
	 * @param text displayed text
	 * @param style text style
	 */
	public static BmlNodeBuilder label(String text, TextStyle style) {
		return label(text).withAttribute("type", style.getType());
	}
	
	/**
	 * Text
	 * @param text displayed text
	 */
	public static BmlNodeBuilder text(String text) {
		return BmlNodeBuilder.builder("text").withAttribute("text", text);
	}
	
	/**
	 * Text
	 * @param text displayed text
	 * @param style text style
	 */
	public static BmlNodeBuilder text(String text, TextStyle style) {
		return text(text).withAttribute("type", style.getType());
	}
	
	/**
	 * Header
	 * @param text displayed txt
	 */
	public static BmlNodeBuilder header(String text) {
		return BmlNodeBuilder.builder("header").withAttribute("text", text);
	}
	
	/**
	 * Align center
	 * @param node child node
	 */
	public static BmlNodeBuilder center(BmlNodeBuilder node) {
		return BmlNodeBuilder.builder("center").withNode(node);
	}
	
	/**
	 * Align left
	 * @param node child node
	 */
	public static BmlNodeBuilder left(BmlNodeBuilder node) {
		return BmlNodeBuilder.builder("left").withNode(node);
	}

	/**
	 * Align right
	 * @param node child node
	 */
	public static BmlNodeBuilder right(BmlNodeBuilder node) {
		return BmlNodeBuilder.builder("right").withNode(node);
	}
	
	/**
	 * Passthrough value
	 * @param id passthrough id
	 * @param text value
	 */
	public static BmlNodeBuilder passthough(String id, String text) {
		return BmlNodeBuilder.builder("passthrough").withAttribute("id", "id").withAttribute("text", text);
	}
	
	/**
	 * Text input 
	 * @param id Input it
	 */
	public static BmlNodeBuilder input(String id) {
		return BmlNodeBuilder.builder("input").withAttribute("id", id);
	}
	
	/**
	 * Dropdown
	 * @param id Dropdown id
	 * @param options comma separated options
	 */
	public static BmlNodeBuilder dropdown(String id, String... options) {
		return BmlNodeBuilder.builder("dropdown").withAttribute("id", id).withAttribute("options", String.join(",", options));
	}
	
	/**
	 * Button
	 * @param id button id
	 * @param text button text
	 */
	public static BmlNodeBuilder button(String id, String text) {
		return BmlNodeBuilder.builder("button").withAttribute("text", text).withAttribute("id", id);
	}
	
	/**
	 * Radio button
	 * @param id radio button id
	 * @param text radio button text
	 */
	public static BmlNodeBuilder radio(String id, String text) {
		return BmlNodeBuilder.builder("radio").withAttribute("id", id).withAttribute("text", text);
	}
	
	/**
	 * Checkbox
	 * @param id checkbox id
	 * @param text checkbox text
	 */
	public static BmlNodeBuilder checkbox(String id, String text) {
		return BmlNodeBuilder.builder("checkbox").withAttribute("id", id).withAttribute("text", text);
	}
	
	/**
	 * Table
	 * @param cols number of columns
	 */
	public static BmlNodeBuilder table(int cols) {
		return BmlNodeBuilder.builder("table").withAttribute("cols", cols);
	}
	
	/**
	 * Horizontal array 
	 */
	public static BmlNodeBuilder harray() {
		return BmlNodeBuilder.builder("harray");
	}
	
	/**
	 * Horizontal array.
	 * @param rescale rescale
	 */
	public static BmlNodeBuilder harray(boolean rescale) {
		return harray().withAttribute("rescale", rescale);
	}
	
	/**
	 * Vertical array
	 */
	private static BmlNodeBuilder varray() {
		return BmlNodeBuilder.builder("varray");
	}
	
	/**
	 * Vertical array
	 * @param rescale
	 */
	public static BmlNodeBuilder varray(boolean rescale) {
		return varray().withAttribute("rescale", rescale);
	}

	
	/**
	 * Scroll area
	 * @param horizontal horizontal scroll
	 * @param vertical vertical scroll
	 * @param node child node
	 */
	public static BmlNodeBuilder scroll(boolean horizontal, boolean vertical, BmlNodeBuilder node) {
		return BmlNodeBuilder.builder("scroll").withAttribute("horizontal", horizontal).withAttribute("vertical", vertical).withNode(node);
	}
	
	/**
	 * Add node to child list
	 * @param nodeBuilder node builder 
	 */
	public BmlBuilder withNode(BmlNodeBuilder nodeBuilder) {
		root.withNode(nodeBuilder);
		return this;
	}
	
	/**
	 * Add nodes to child list
	 * @param nodes nodes
	 */
	public BmlBuilder withNodes(BmlBuilder nodes) {
		root.withNodes(nodes);
		return this;
	}
	
	/**
	 * Create BML
	 * @return BML
	 */
	public String buildBml() {
		StringBuilder builder = new StringBuilder();
		root.buildNodes().forEach(node -> builder.append(node.buildBml()));
		return builder.toString();
	}
	
	/**
	 * Create nodes.
	 * @return List of node builders
	 */
	public List<BmlNodeBuilder> getNodeBuilders() {
		return root.buildNodes();
	}
	
	/**
	 * Build a node as DOM element
	 * @return node 
	 */
	public Element build(Document document) {
		return root.build(document);
	}
}
