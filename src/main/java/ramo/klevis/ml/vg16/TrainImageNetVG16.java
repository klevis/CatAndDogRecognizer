package ramo.klevis.ml.vg16;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by klevis.ramo on 12/26/2017.
 */
public class TrainImageNetVG16 {
    private static final long seed = 12345;
    public static final Random randNumGen = new Random(seed);
    public static final String[] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;

    private static final int TRAIN_LOAD_SIZE = 85;
    private static final int NUM_POSSIBLE_LABELS = 2;
    private static final int SAVED_INTERVAL = 100;
    private static int BATCH_SIZE = 16;
    private static final int EPOCH = 5;

    public static String DATA_PATH = "resources";
    public static final String TRAIN_FOLDER = DATA_PATH + "/train_both";
    public static final String TEST_FOLDER = DATA_PATH + "/test_both";

    private static final String featurizeExtractionLayer = "fc2";
    private static final String SAVING_PATH = DATA_PATH + "/saved/modelIteration_";

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TrainImageNetVG16.class);
    public static ParentPathLabelGenerator LABEL_GENERATOR_MAKER = new ParentPathLabelGenerator();
    public static BalancedPathFilter PATH_FILTER = new BalancedPathFilter(randNumGen, allowedExtensions, LABEL_GENERATOR_MAKER);

    public static void main(String[] args) throws IOException {
        ZooModel zooModel = new VGG16();
        ComputationGraph preTrainedNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        log.info(preTrainedNet.summary());

        // Define the File Paths
        File trainData = new File(TRAIN_FOLDER);
        File testData = new File(TEST_FOLDER);
        FileSplit train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        FileSplit test = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);


        InputSplit[] sample = train.sample(PATH_FILTER, TRAIN_LOAD_SIZE, 100 - TRAIN_LOAD_SIZE);
        DataSetIterator trainIterator = getDataSetIterator(sample[0]);
        DataSetIterator devIterator = getDataSetIterator(sample[1]);


        FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .learningRate(5e-5)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS)
                .seed(seed)
                .build();

        ComputationGraph vgg16Transfer = new TransferLearning.GraphBuilder(preTrainedNet)
                .fineTuneConfiguration(fineTuneConf)
                .setFeatureExtractor(featurizeExtractionLayer)
                .removeVertexKeepConnections("predictions")
                .addLayer("predictions",
                        new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nIn(4096).nOut(NUM_POSSIBLE_LABELS)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.SOFTMAX).build(), featurizeExtractionLayer)
                .build();
        vgg16Transfer.setListeners(new ScoreIterationListener(5));
        log.info(vgg16Transfer.summary());

        DataSetIterator testIterator = getDataSetIterator(test.sample(PATH_FILTER, 1, 0)[0]);
        int iEpoch = 0;
        int i = 0;
        while (iEpoch < EPOCH) {
            while (trainIterator.hasNext()) {
                DataSet trained = trainIterator.next();
                vgg16Transfer.fit(trained);
                if (i % SAVED_INTERVAL == 0 && i != 0) {

                    ModelSerializer.writeModel(vgg16Transfer, new File(SAVING_PATH + i + "epoch_" + iEpoch + ".zip"), false);
                    evalOn(vgg16Transfer, devIterator, i);
                }
                i++;
            }

            trainIterator.reset();
            iEpoch++;

            evalOn(vgg16Transfer, testIterator, iEpoch);
        }
    }


    public static void evalOn(ComputationGraph vgg16Transfer, DataSetIterator testIterator, int iEpoch) throws IOException {

        log.info("Evaluate model at iter " + iEpoch + " ....");
        Evaluation eval = vgg16Transfer.evaluate(testIterator);
        log.info(eval.stats());
        testIterator.reset();

    }

    public static DataSetIterator getDataSetIterator(InputSplit sample) throws IOException {
        ImageRecordReader imageRecordReader = new ImageRecordReader(224, 224, 3, LABEL_GENERATOR_MAKER);
        imageRecordReader.initialize(sample);

        DataSetIterator iterator = new RecordReaderDataSetIterator(imageRecordReader, BATCH_SIZE, 1, NUM_POSSIBLE_LABELS);
        iterator.setPreProcessor(new VGG16ImagePreProcessor());
        return iterator;
    }


}
