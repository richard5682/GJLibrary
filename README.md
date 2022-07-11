# GJLibrary
This is the main repositories for GJLibrary, that is the main library for NodalNET

# Documentation
GJLibrary provide new looks and object oriented graphics as well as action handling, but the main focus of this library is for integrating a trained Neural Network from NodalNet into your Java application, using the GJNeural we will show you how to load the data from your .NNET file and use the functions in the library to simulate Neural Network.

# Guide
## GJNeural Useful Class
- Conv2DNet.java - this is the main library for Convolutional Network.
  - ConvNetwork class - this class contain functions for simulating and loading in the neural network data
    - ConvNetwork ConvNetwork.LoadData(String data, LogBackAction logback)
      - Returns ConvNetwork class with loaded data
      - data - String representation of the network data
      - logback - create a feedback for the loading process (null if not needed)
    - float[] ConvNetwork.GetOutput(float[][][] data2d)
      - Return the output of the network in array of float, with the same order as how it was trained.
      - data2d - a 3 dimensional float array which contains all the images, the channels, the pixels data of an image, generated on TrainingData class using ConvertData2D() function.
- TrainingData.java - this is the main library for processess in training data.
  - TrainingData class - a class for storing image_data, outputs and data_names useful for training a neural network. But for integrating an NNET file the only use for the trainingdata is storing the input image data.
    - BufferedImage TrainingData.process_image - store the BufferedImage that will be input into the Neural Network.
    - float[][][] TrainingData.data2d - field for storing the data2d data from the process_image, use ConvertData2D() function to generate this data2d data.
    - void TrainingData.ConvertData2D() - function for converting the proccess_image buffered image into array of data the NNET can process.
    
## Examples on Loading Network

```java
  // CODE FOR LOADING A NNET FILE
  public static void OpenNetwork() {
	String link = GJFileLoader.OpenLoadChooser(null, main_frame);
	if(link != null) {
			String netdata = GJFileLoader.LoadText(link)[0];
			network = ConvNetwork.LoadData(netdata, null);
	}
  }
```
  - This use GJFileLoader library to get the link of the NNET file and load the data, then using ConvNetwork.LoadData(netdata,null) function to generate the ConvNetwork Object with those data.

```java
	// CODE FOR INITIALIZING A TRAINING DATA
  	TrainingData data = new TrainingData();
	BufferedImage resize_image = GJColorProcess.ResizeImage(image, network.input_size, network.input_size);
	data.processed_image = resize_image;
	data.output_type = 1;
	data.ConvertData2D();
	float[] output = network.GetOutput(data.data2D);
```
  - Initializing an empty TrainingData then setting the processed_image and output_type=1.
  - Using ConvertData2D() function to get the data2d with the given processed_image.
  - Get the output using network.GetOutput(data.data2D)
```java
// CODE FOR PROCESSING THE OUTPUT
	if(output[0]>0.5) {//LOOKING
		if(paused) {
			paused = false;
			click();
		}	
	}else {//NOTLOOKING
		if(!paused) {
			paused = true;
			click();
		}
	}
```
- Here since I only have 2 output and I the network configuration is in softmax therefore the combined output when we add them is equal to 1, therefore to get the highest output i just need to check if the output is greater than 0.5.
