import star.base.neo.DoubleVector;
import star.base.report.ExpressionReport;
import star.base.report.SumReport;
import star.common.*;
import star.coremodule.objectselector.ModelDescriptor;
import star.coremodule.objectselector.ObjectSelectorField;
import star.coremodule.objectselector.SelectorDescriptor;
import star.coremodule.objectselector.SelectorUtils;
import star.coremodule.ui.MessageDialog;
import star.energy.TotalTemperatureProfile;
import star.flow.MassFlowAverageReport;
import star.flow.MassFlowRateProfile;
import star.radiation.common.RadiationTemperatureProfile;
import star.species.MassFractionProfile;
import star.vis.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

public class ExportScenesAndPlots extends StarMacro {
	public Simulation sim;

    public void execute() {

		sim = getActiveSimulation();

		JFrame frame = new JFrame("Steady state exporter");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new GridBagLayout());

		//Create and set up the content pane.
		MyContentPane myContentPane = new MyContentPane();
		frame.setContentPane(myContentPane);

		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setMinimumSize(frame.getSize());
		if (System.getProperty("os.name").contains("Linux")) {
			frame.setAlwaysOnTop(true);
		}
		frame.setVisible(true);
        

    }

    private SimpleAnnotation createAnnotation(String str, double rang){

		SimpleAnnotation simpleAnnotation =
				sim.getAnnotationManager().createAnnotation(SimpleAnnotation.class);

		simpleAnnotation.setPresentationName(str);

		simpleAnnotation.setText(String.format("%s: %.1f", str, rang));

		return simpleAnnotation;



	}

	private class MyContentPane extends JPanel implements ActionListener {

		protected ModelDescriptor modelDescriptorScenes;
		protected ObjectSelectorField sceneSelector;
		protected Collection<Scene> exportScenes;

		public MyContentPane() {

			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(3, 3, 3, 3);
			c.weightx = 1;

			// Boundary selection panel
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			JPanel panel1 = new JPanel();
			panel1.setLayout(new GridBagLayout());
			panel1.setBorder(BorderFactory.createTitledBorder("Select scenes"));

			// Predefined selection controls for boundaries
			c.fill = GridBagConstraints.HORIZONTAL;
			panel1.add(new JLabel("Scenes:"), c);

			modelDescriptorScenes = new SelectorDescriptor.Builder(sim,
					FilterModel.SceneFilterModel).build();
			sceneSelector = new ObjectSelectorField(modelDescriptorScenes);
			sceneSelector.setPreferredSize(new Dimension(300, 25));
			c.gridy = 1;
			panel1.add(sceneSelector, c);

			// A button to display selected boundaries in scene
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 1;
			JButton sceneButton = new JButton("Export");
			sceneButton.setActionCommand("export");
			sceneButton.addActionListener(this);
			sceneButton.setToolTipText("export scenes");
			panel1.add(sceneButton, c);

			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			this.add(panel1, c);
		}

		public void actionPerformed(ActionEvent e) {
			if ("export".equals(e.getActionCommand())) {
				if (!sceneSelector.hasSelection()) {
					MessageDialog warning = new MessageDialog("No scenes selected");
					warning.show();
				} else {
					exportScenes = SelectorUtils.createObjects(sim, sceneSelector.getSelected());
					RunExportScenes(exportScenes);
				}
			}
		}

		public void RunExportScenes(Collection<Scene> sceneCollection){
		String dir = sim.getSessionDir(); //get the name of the simulation's directory
		String sep = System.getProperty("file.separator"); //get the right separator for your operative system
		//File resultFolder = new File(dir + sep + sim + "_Results");
	  	//resultFolder.mkdir();

		File sceneFolder = new File(dir + sep + sim +"_Scenes");
	  	sceneFolder.mkdir();

		//File plotFolder = new File(resultFolder + "\\Plots");
	  	//plotFolder.mkdir();


        for (Scene scn: sceneCollection) {

			LogoAnnotation logoAnnotation =
					((LogoAnnotation) sim.getAnnotationManager().getObject("Logo"));

			ScalarDisplayer scalarDisplayer =
					((ScalarDisplayer) scn.getDisplayerManager().getObject("Scalar 1"));

			SimpleAnnotation maxAnn = createAnnotation(scalarDisplayer.getScalarDisplayQuantity().getFieldFunctionName() + "_max",
					scalarDisplayer.getScalarDisplayQuantity().getGlobalMax());
			SimpleAnnotation minAnn = createAnnotation(scalarDisplayer.getScalarDisplayQuantity().getFieldFunctionName() + "_min",
					scalarDisplayer.getScalarDisplayQuantity().getGlobalMin());

			scn.getAnnotationPropManager().getAnnotationGroup().setObjects(maxAnn, minAnn, logoAnnotation);

			SimpleAnnotationProp simpleAnnotationProp_max =
					((SimpleAnnotationProp) scn.getAnnotationPropManager().getObject(scalarDisplayer.getScalarDisplayQuantity().getFieldFunctionName() + "_max"));
			simpleAnnotationProp_max.setPosition(new DoubleVector(new double[] {0.0, 0.0, 0.0}));

			SimpleAnnotationProp simpleAnnotationProp_min =
					((SimpleAnnotationProp) scn.getAnnotationPropManager().getObject(scalarDisplayer.getScalarDisplayQuantity().getFieldFunctionName() + "_min"));
			simpleAnnotationProp_min.setPosition(new DoubleVector(new double[] {0.35, 0.0, 0.0}));

			//sim.println("Saving Scene: " + scn.getPresentationName());
			//scn.printAndWait(resolvePath(sceneFolder + sep + scn.getPresentationName() + ".jpg"), 1, 1920, 1080);


		}

//	  for (StarPlot plt : sim.getPlotManager().getObjects()) {
//	      sim.println("Saving Plot: " + plt.getPresentationName());
//	      plt.encode(resolvePath(plotFolder + sep + plt.getPresentationName() + ".jpg"), "jpg", 2560, 1440);
//	  }
	}
	}
}