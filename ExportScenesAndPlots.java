import star.base.neo.DoubleVector;
import star.common.*;
import star.vis.*;
import java.io.File;
import java.util.Scanner; 

public class ExportScenesAndPlots extends StarMacro {
	public Simulation sim;

    public void execute() {

		sim = getActiveSimulation();
        
        String dir = sim.getSessionDir(); //get the name of the simulation's directory
        String sep = System.getProperty("file.separator"); //get the right separator for your operative system
	  File resultFolder = new File(dir + "\\" + sim + "_Results");
	  resultFolder.mkdir();

	  File sceneFolder = new File(resultFolder + "\\Scenes");
	  sceneFolder.mkdir();

	  File plotFolder = new File(resultFolder + "\\Plots");
	  plotFolder.mkdir();
              

        for (Scene scn: sim.getSceneManager().getScenes()) {


			LogoAnnotation logoAnnotation =
					((LogoAnnotation) sim.getAnnotationManager().getObject("Logo"));

			ScalarDisplayer scalarDisplayer =
					((ScalarDisplayer) scn.getDisplayerManager().getObject("Scalar 1"));

			SimpleAnnotation maxAnn = createAnnotation(scn.getPresentationName() + "_max", scalarDisplayer.getScalarDisplayQuantity().getRangeMax());
			SimpleAnnotation minAnn = createAnnotation(scn.getPresentationName() + "_min", scalarDisplayer.getScalarDisplayQuantity().getRangeMin());

			scn.getAnnotationPropManager().getAnnotationGroup().setObjects(maxAnn, minAnn, logoAnnotation);

			SimpleAnnotationProp simpleAnnotationProp_max =
					((SimpleAnnotationProp) scn.getAnnotationPropManager().getObject(scn.getPresentationName() + "_max"));
			simpleAnnotationProp_max.setPosition(new DoubleVector(new double[] {0.1, 1.0, 0.0}));

			SimpleAnnotationProp simpleAnnotationProp_min =
					((SimpleAnnotationProp) scn.getAnnotationPropManager().getObject(scn.getPresentationName() + "_min"));
			simpleAnnotationProp_min.setPosition(new DoubleVector(new double[] {0.4, 1.0, 0.0}));

			sim.println("Saving Scene: " + scn.getPresentationName());
	      	scn.printAndWait(resolvePath(sceneFolder + sep + scn.getPresentationName() + ".jpg"), 1, 1920, 1080);
		
	   	
        }

//	  for (StarPlot plt : sim.getPlotManager().getObjects()) {
//	      sim.println("Saving Plot: " + plt.getPresentationName());
//	      plt.encode(resolvePath(plotFolder + sep + plt.getPresentationName() + ".jpg"), "jpg", 2560, 1440);
//	  }
    }

    private SimpleAnnotation createAnnotation(String str, double rang){

		SimpleAnnotation simpleAnnotation =
				sim.getAnnotationManager().createAnnotation(SimpleAnnotation.class);

		simpleAnnotation.setPresentationName(str);

		simpleAnnotation.setText(String.format("%s: %.3f", str, rang));

		return simpleAnnotation;



	}
}