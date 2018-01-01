package ramo.klevis;

import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static ramo.klevis.TrainImageNetVG16.*;

/**
 * Created by klevis.ramo on 12/28/2017.
 */
public class TestVG16ForCat {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TrainImageNetVG16.class);

    public static void main(String[] args) throws IOException {
        ComputationGraph computationGraph = ModelSerializer.restoreComputationGraph(new File(DATA_PATH + "/saved/RunEpoch_10_32_" + 2200 + "_old.zip"));
        File trainData = new File(TEST_FOLDER);
        FileSplit test = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        InputSplit inputSplit = test.sample(PATH_FILTER, 1)[0];
        DataSetIterator dataSetIterator = getDataSetIterator(inputSplit);
        evalOn(computationGraph, dataSetIterator, 1);

    }
}
