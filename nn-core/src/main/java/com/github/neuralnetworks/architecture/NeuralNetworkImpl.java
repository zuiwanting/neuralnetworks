package com.github.neuralnetworks.architecture;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.neuralnetworks.calculation.LayerCalculator;
import com.github.neuralnetworks.util.UniqueList;
import com.github.neuralnetworks.util.Util;

/**
 * Base class for all types of neural networks. A neural network is defined only
 * by the layers it contains. The layers themselves contain the connections with
 * the other layers.
 */
public class NeuralNetworkImpl implements NeuralNetwork {

    private static final long serialVersionUID = 1L;

    private Set<Layer> layers;
    private LayerCalculator layerCalculator;

    public NeuralNetworkImpl() {
	super();
	this.layers = new UniqueList<Layer>();
    }

    public NeuralNetworkImpl(List<Layer> layers) {
	super();
	this.layers = new UniqueList<Layer>(layers);
    }

    @Override
    public LayerCalculator getLayerCalculator() {
	return layerCalculator;
    }

    public void setLayerCalculator(LayerCalculator layerCalculator) {
	this.layerCalculator = layerCalculator;
    }

    @Override
    public Set<Layer> getLayers() {
	return layers;
    }

    public void setLayers(Set<Layer> layers) {
	this.layers = layers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.neuralnetworks.architecture.NeuralNetwork#getInputLayer()
     * Default implementation - the input layer is that layer, which doesn't
     * have any inbound connections
     */
    @Override
    public Layer getInputLayer() {
	return layers.stream().filter(l -> l.getConnections(this).stream().noneMatch(c -> l == c.getOutputLayer() && !Util.isBias(c.getInputLayer()))).findFirst().get();
    }

    @Override
    public Layer getOutputLayer() {
	return getNoOutboundConnectionsLayer();
    }

    protected Layer getNoOutboundConnectionsLayer() {
	return layers.stream().filter(l -> l.getConnections(this).stream().noneMatch(c -> l == c.getInputLayer())).findFirst().get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.neuralnetworks.architecture.NeuralNetwork#getConnections()
     * Returns list of all the connections within the network. The list is
     * retrieved by iterating over all the layers. Only connections that have
     * both layers in this network are returned.
     */
    @Override
    public List<Connections> getConnections() {
	List<Connections> result = new UniqueList<>();
	if (layers != null) {
	    layers.forEach(l -> result.addAll(l.getConnections(this)));
	}

	return result;
    }

    /**
     * @param inputLayer
     * @param outputLayer
     * @return Connection between the two layers if it exists
     */
    public Connections getConnection(Layer inputLayer, Layer outputLayer) {
	return getConnections().stream().filter(c -> (c.getInputLayer() == inputLayer && c.getOutputLayer() == outputLayer) || (c.getInputLayer() == outputLayer && c.getOutputLayer() == inputLayer)).findFirst().get();
    }

    /**
     * Add layer to the network
     * 
     * @param layer
     * @return whether the layer was added successfully
     */
    public boolean addLayer(Layer layer) {
	if (layer != null) {
	    if (layers == null) {
		layers = new UniqueList<>();
	    }

	    if (!layers.contains(layer)) {
		layers.add(layer);
		return true;
	    }
	}

	return false;
    }

    /**
     * Remove layer from the network
     * 
     * @param layer
     */
    public void removeLayer(Layer layer) {
	if (layer != null) {
	    if (layers != null) {
		// remove layer and bias layers
		layers.remove(layer);
		layer.getConnections(this).stream().map(Connections::getInputLayer).filter(l -> Util.isBias(l)).forEach(l -> layers.remove(l));
	    }
	}
    }

    /**
     * Add layers to the network
     * 
     * @param newLayers
     */
    public void addLayers(Collection<Layer> newLayers) {
	if (newLayers != null) {
	    if (layers == null) {
		layers = new UniqueList<>();
	    }

	    newLayers.stream().filter(l -> !layers.contains(l)).forEach(l -> layers.add(l));
	}
    }

    /**
     * Add connection to the network - this means adding both input and output
     * layers to the network
     * 
     * @param connection
     */
    public void addConnection(Connections connection) {
	if (connection != null) {
	    addLayer(connection.getInputLayer());
	    addLayer(connection.getOutputLayer());
	}
    }
}
