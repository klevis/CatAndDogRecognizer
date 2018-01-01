package ramo.klevis.ml.vg16;

import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by klevis.ramo on 12/28/2017.
 */
public class VG16ForCat {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TrainImageNetVG16.class);
    private static final String TRAINED_PATH_MODEL = TrainImageNetVG16.DATA_PATH + "/saved/RunEpoch_10_32_" + 660 + "_old.zip";
    private static ComputationGraph computationGraph;

    public static void main(String[] args) throws IOException {
        new VG16ForCat().runOnTestSet();
    }


    public boolean detectCat(File file) throws IOException {
        if (computationGraph == null) {
            computationGraph = loadModel();
        }
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray image = loader.asMatrix(new FileInputStream(file));
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);
        INDArray[] output = computationGraph.output(false, image);
        System.out.println("output = " + output);
        return output[0].getDouble(0) > 0.5 ? true : false;
    }

    private void runOnTestSet() throws IOException {
        ComputationGraph computationGraph = loadModel();
        File trainData = new File(TrainImageNetVG16.TEST_FOLDER);
        FileSplit test = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, TrainImageNetVG16.randNumGen);
        InputSplit inputSplit = test.sample(TrainImageNetVG16.PATH_FILTER, 1)[0];
        DataSetIterator dataSetIterator = TrainImageNetVG16.getDataSetIterator(inputSplit);
        TrainImageNetVG16.evalOn(computationGraph, dataSetIterator, 1);
    }

    public ComputationGraph loadModel() throws IOException {
        computationGraph = ModelSerializer.restoreComputationGraph(new File(TRAINED_PATH_MODEL));
        return computationGraph;
    }
}
