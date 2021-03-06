/*
 * Copyright (C) 2013-2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */
package com.intel.tools.fdk.graphframework.layout;

import java.util.stream.Stream;

import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.presenter.DefaultPresenterManager;
import com.intel.tools.fdk.graphframework.figure.presenter.IPresenterManager;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.figure.presenter.Presenter;
import com.intel.tools.fdk.graphframework.graph.INode;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;

/**
 * Graph layout which choose a position for each graph node.
 *
 * The algorithm used is the one defined in {@link AutoLayoutComputer}.
 */
public class AutoLayoutGenerator extends LayoutGenerator {

    /** Max element width, initialized at 40 to potentially separate empty groups */
    private int widthMax = 40;
    /** Max element height, initialized at 40 to potentially separate empty groups */
    private int heightMax = 40;

    /**
     * Create a layout generator which initialize the displayed graph with position computed through a dedicated
     * algorithm. </br>
     *
     * After a graph update notification, the layout algorithm will not be computed again. Only the original graph is
     * computed.
     *
     * @param adapter
     *            the model adapter which provide the graph
     * @param displayer
     *            the graph displayer to use
     */
    public AutoLayoutGenerator(final IAdapter adapter, final GraphDisplayer displayer) {
        this(adapter, new DefaultPresenterManager(), displayer);
    }

    /**
     * Create a layout generator which initialize the displayed graph with position computed through a dedicated
     * algorithm. </br>
     *
     * After a graph update notification, the layout algorithm will not be computed again. Only the original graph is
     * computed.
     *
     * @param adapter
     *            the model adapter which provide the graph
     * @param presenterManager
     *            The presenter manager responsible for creating presenters associated with graph nodes.
     * @param displayer
     *            the graph displayer to use
     */
    public AutoLayoutGenerator(final IAdapter adapter, final IPresenterManager presenterManager,
            final GraphDisplayer displayer) {
        super(adapter, presenterManager, displayer);
        // run the first layout
        layout();
    }

    /**
     * Compute the graph to calculate elements location
     */
    public void layout() {
        // The first display has been done, let's compute initial positions.
        final AutoGroupLayoutComputer computer = new AutoGroupLayoutComputer(getGraph());
        for (final LeafPresenter presenters : getLeafPresenters()) {
            final Rectangle bounds = presenters.getBoundsFigure().getBounds();
            widthMax = bounds.width > widthMax ? bounds.width : widthMax;
            heightMax = bounds.height > heightMax ? bounds.height : heightMax;
        }

        Stream.concat(getLeafPresenters().stream(),
                      getGroupPresenters().stream().filter(presenter -> presenter.getNode().getLeaves().isEmpty()))
                .forEach(presenter -> setupPresenter(presenter, computer.getCoordinate(presenter.getNode())));
    }

    private void setupPresenter(final Presenter<? extends INode> presenter, final PrecisionPoint coordinates) {
        /**
         * Ordinates are negated because draw2d uses the upper left corner as origin but the algorithm uses a standard
         * cartesian coordinates (ordinates grows towards the upper side of the view).
         */
        presenter.getBoundsFigure().setLocation(
                new PrecisionPoint(coordinates.x * widthMax * 1.5, -coordinates.y * heightMax * 2));
    }

}
