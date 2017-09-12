package org.gotti.wurmunlimited.modsupport.bml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BmlNodeBuilder {

	private final String type;
	private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
	private List<BmlNodeBuilder> nodes = new ArrayList<>();
	
	private BmlNodeBuilder(String type) {
		this.type = type;
	}

	public static BmlNodeBuilder builder(String type) {
		return new BmlNodeBuilder(type);
	}

	public BmlNodeBuilder withAttribute(String name, String value) {
		attributes.put(name, value);
		return this;
	}
	
	public BmlNodeBuilder withAttribute(String name, int value) {
		attributes.put(name, String.valueOf(value));
		return this;
	}

	public BmlNodeBuilder withAttribute(String name, boolean value) {
		attributes.put(name, String.valueOf(value));
		return this;
	}
	
	public BmlNodeBuilder red() {
		return color(255, 0, 0);
	}
	
	public BmlNodeBuilder green() {
		return color(0, 255, 0);
	}
	
	public BmlNodeBuilder blue() {
		return color(0, 0, 255);
	}
	
	public BmlNodeBuilder color(int red, int green, int blue) {
		return withAttribute("color", String.join(",", String.valueOf(red), String.valueOf(green), String.valueOf(blue)));
	}
	
	public BmlNodeBuilder size(int width, int height) {
		return withAttribute("size", String.join(",", String.valueOf(width), String.valueOf(height)));
	}

	public Element build(Document document) {
		Element element = document.createElement(type);
		attributes.forEach((name, value) -> element.setAttribute(name, value));
		nodes.forEach(child -> element.appendChild(child.build(document)));
		return element;
	}

	public BmlNodeBuilder withNode(BmlNodeBuilder nodeBuilder) {
		if (nodeBuilder == null) {
			return withNode("null");
		} else {
			nodes.add(nodeBuilder);
			return this;
		}
	}

	public BmlNodeBuilder withNode(String type) {
		return withNode(BmlNodeBuilder.builder(type));
	}

	public BmlNodeBuilder withNodes(BmlBuilder bmlBuilder) {
		nodes.addAll(bmlBuilder.getNodeBuilders());
		return this;
	}

	public List<BmlNodeBuilder> buildNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public String buildBml() {
		StringBuilder builder = new StringBuilder();
		if (!nodes.isEmpty() || !attributes.isEmpty()) {
			builder.append(type);
			builder.append("{");
			attributes.forEach((name, value) -> builder.append(name + "=\"" + value + "\";"));
			nodes.forEach(node -> builder.append(node.buildBml()));
			builder.append("}");
		} else {
			builder.append(type + ";");
		}
		return builder.toString();
	}
}
