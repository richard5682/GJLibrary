package GJImageProcessing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class GJColorProcess {
	public static final String[] KERNEL_CHOICE = {"NONE","SHARPEN3x3","VERTICAL3x3","HORIZONTAL3x3","VERTICALSOBEL3x3","HORIZONTALSOBEL3x3","AVERAGING3x3","NOISE3x3"};
	public static final String[] POOLING_CHOICE = {"MAXPOOLING","MINPOOLING","MEANPOOLING"};
	public static float[][] FILTER3x3_SHARPENKERNEL = {
			{0f,-1f,0f},
			{-1f,5f,-1f},
			{0f,-1f,0f}};
	public static float[][] FILTER3x3_VERTICALKERNEL = {
			{1f,0f,-1f},
			{1f,0f,-1f},
			{1f,0f,-1f}};
	public static float[][] FILTER3x3_HORIZONTALKERNEL = {
			{1f,1f,1f},
			{0f,0f,0f},
			{-1f,-1f,-1f}};
	public static float[][] FILTER3x3_VERTICALSOBEL = {
			{-1f,0f,1f},
			{-2f,0f,2f},
			{-1f,0f,1f}};
	public static float[][] FILTER3x3_HORIZONTALSOBEL = {
			{-1f,-2f,-1f},
			{0f,0f,0f},
			{1f,2f,1f}};
	public static float[][] FILTER3x3_AVERAGING = {
			{1f/16,2f/16,1f/16},
			{2f/16,4f/16,2f/16},
			{1f/16,2f/16,1f/16}};
	public static float[][] FILTER3x3_NOISEREMOVAL1 = {
			{0,1f,0},
			{1f,1f,1f},
			{0,1f,0f}};
	public static final int REDCHANNEL=0,GREENCHANNEL=1,BLUECHANNEL=2,MAXPOOLING=0,MEANPOOLING=2,MINPOOLING = 1;
	public static final Kernel SHARPENKERNEL3x3 = new Kernel(FILTER3x3_SHARPENKERNEL,0f);
	public static final Kernel VERTICALKERNEL3x3 = new Kernel(FILTER3x3_VERTICALKERNEL,0);
	public static final Kernel HORIZONTALKERNEL3x3 = new Kernel(FILTER3x3_HORIZONTALKERNEL,0);
	public static final Kernel VERTICALSOBEL3x3 = new Kernel(FILTER3x3_VERTICALSOBEL,0);
	public static final Kernel HORIZONTALSOBEL3x3 = new Kernel(FILTER3x3_HORIZONTALSOBEL,0);
	public static final Kernel NOISEREDUCTION3x3 = new Kernel(FILTER3x3_NOISEREMOVAL1,-3);
	public static final Kernel AVERAGING3x3 = new Kernel(FILTER3x3_AVERAGING,0);
	public static class Kernel{
		float[][] kernel;
		float bias;
		public Kernel(float[][] kernel,float bias){
			this.kernel = kernel;
			this.bias = bias;
		}
	}
	public static Kernel GetKernel(int index){
		if(index == 0){
			return null;
		}else if(index == 1){
			return SHARPENKERNEL3x3;
		}else if(index == 2){
			return VERTICALKERNEL3x3;
		}else if(index == 3){
			return HORIZONTALKERNEL3x3;
		}else if(index == 4){
			return VERTICALSOBEL3x3;
		}else if(index == 5){
			return HORIZONTALSOBEL3x3;	
		}else if(index == 6){
			return AVERAGING3x3;	
		}else if(index == 7){
			return NOISEREDUCTION3x3;
		}else{
			return null;
		}
	}
	public static Color Invert(Color c){
		return new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue());
	}
	public static int Truncate(float value){
		if(value > 255){
			value = 255;
		}else if(value < 0){
			value = 0;
		}
		return (int)value;
	}
	public static BufferedImage CropImage(BufferedImage image,boolean high_trigger,int trigger){
		int x0=0,y0=0;
		int x1=10,y1=10;
		outerloop:
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int red = Truncate(c.getRed());
				if(high_trigger){
					if(red > trigger){
						x0=x;
						break outerloop;
					}
				}else{
					if(red < trigger){
						x0=x;
						break outerloop;
					}
				}
			}
		}
		outerloop:
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				Color c = new Color(image.getRGB(x, y));
				int red = Truncate(c.getRed());
				if(high_trigger){
					if(red > trigger){
						y0=y;
						break outerloop;
					}
				}else{
					if(red < trigger){
						y0=y;
						break outerloop;
					}
				}
			}
		}
		outerloop:
			for(int y=image.getHeight()-1;y>=0;y--){
				for(int x=image.getWidth()-1;x>=0;x--){
					Color c = new Color(image.getRGB(x, y));
					int red = Truncate(c.getRed());
					if(high_trigger){
						if(red > trigger){
							y1=y;
							break outerloop;
						}
					}else{
						if(red < trigger){
							y1=y;
							break outerloop;
						}
					}
				}
			}
		outerloop:
			for(int x=image.getWidth()-1;x>=0;x--){
				for(int y=image.getHeight()-1;y>=0;y--){
					Color c = new Color(image.getRGB(x, y));
					int red = Truncate(c.getRed());
					if(high_trigger){
						if(red > trigger){
							x1=x;
							break outerloop;
						}
					}else{
						if(red < trigger){
							x1=x;
							break outerloop;
						}
					}
				}
			}
			BufferedImage returnimage = new BufferedImage(x1-x0+1,y1-y0+1,BufferedImage.TYPE_INT_RGB);
			for(int y=y0;y<y1;y++){
				for(int x=x0;x<x1;x++){
					Color c = new Color(image.getRGB(x, y));
					returnimage.setRGB(x-x0, y-y0, c.getRGB());
				}
			}
			return returnimage;
	}
	public static BufferedImage CloneImage(BufferedImage image){
		BufferedImage returnimage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				Color c = new Color(image.getRGB(x, y));
				returnimage.setRGB(x, y, c.getRGB());
			}
		}
		return returnimage;
	}
	public static BufferedImage InverseImage(BufferedImage image){
		BufferedImage newimage = CloneImage(image);
		for(int x=0;x<newimage.getWidth();x++){
			for(int y=0;y<newimage.getHeight();y++){
				Color c = new Color(newimage.getRGB(x, y));
				int red = Truncate(c.getRed());
				int green = Truncate(c.getGreen());
				int blue = Truncate(c.getBlue());
				Color newcolor = new Color(255-red,255-green,255-blue);
				newimage.setRGB(x, y, newcolor.getRGB());
			}
		}
		return newimage;
	}
	public static BufferedImage ConvertBW(BufferedImage image){
		BufferedImage newimage = CloneImage(image);
		for(int x=0;x<newimage.getWidth();x++){
			for(int y=0;y<newimage.getHeight();y++){
				Color c = new Color(newimage.getRGB(x, y));
				int value = (c.getRed()+c.getBlue()+c.getGreen())/3;
				Color newcolor = new Color(value,value,value);
				newimage.setRGB(x, y, newcolor.getRGB());
			}
		}
		return newimage;
	}
	public static BufferedImage AddImage(BufferedImage image1,BufferedImage image2){
		BufferedImage resultimage = new BufferedImage(image1.getWidth(),image1.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<resultimage.getWidth();x++){
			for(int y=0;y<resultimage.getHeight();y++){
				Color c1 = new Color(image1.getRGB(x, y));
				Color c2 = new Color(image2.getRGB(x, y));
				int red = Truncate(c1.getRed()+c2.getRed());
				int green = Truncate(c1.getGreen()+c2.getGreen());
				int blue = Truncate(c1.getBlue()+c2.getBlue());
				Color newcolor = new Color(red,green,blue);
				resultimage.setRGB(x, y, newcolor.getRGB());
			}
		}
		return resultimage;
	}
	public static void ColorFilterImage(BufferedImage image,boolean red,boolean green,boolean blue){
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int r=0,g=0,b=0;
				if(red){
					r=c.getRed();
				}
				if(blue){
					b=c.getBlue();
				}
				if(green){
					g=c.getGreen();
				}
				Color newcolor = new Color(r,g,b);
				image.setRGB(x, y, newcolor.getRGB());
			}
		}
	}
	public static BufferedImage ImageToBufferedImage(Image img){
		 BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		 Graphics2D bGr = bimage.createGraphics();
		 bGr.drawImage(img, 0, 0, null);
		 bGr.dispose();
		 return bimage;
	}
	public static BufferedImage ResizeImage(BufferedImage image,int width,int height){
		BufferedImage resize_image = ImageToBufferedImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT));
		return resize_image;
	}
	 private static BufferedImage convertToARGB(BufferedImage image)
	    {
	        BufferedImage newImage = new BufferedImage(
	            image.getWidth(), image.getHeight(),
	            BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = newImage.createGraphics();
	        g.drawImage(image, 0, 0, null);
	        g.dispose();
	        return newImage;
	    }    
	public static BufferedImage createFlipped(BufferedImage image,int horizontalscale,int verticalscale)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        return createTransformed(image, at);
    }

    public static BufferedImage createRotated(BufferedImage image)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
            Math.PI, image.getWidth()/2, image.getHeight()/2.0);
        return createTransformed(image, at);
    }

    public static BufferedImage createTransformed(
        BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(
            image.getWidth(), image.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
	public static float[][] GetChannel(BufferedImage image,int channel,boolean normalize){
		float[][] image_data = new float[image.getWidth()][image.getHeight()];
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				float redvalue = (float)c.getRed();
				float greenvalue = (float)c.getGreen();
				float bluevalue = (float)c.getBlue();
				if(normalize){
					redvalue = (float)c.getRed()/255f;
					greenvalue = (float)c.getGreen()/255f;
					bluevalue = (float)c.getBlue()/255f;
				}
				if(channel == REDCHANNEL){
					image_data[x][y] = redvalue;
				}else if(channel == GREENCHANNEL){
					image_data[x][y] = greenvalue;
				}else if(channel == BLUECHANNEL){
					image_data[x][y] = bluevalue;
				}
			}
		}
		return image_data;
	}
	public static BufferedImage PoolImage(BufferedImage image, int size,int type_pooling,boolean rc,boolean gc,boolean bc,boolean bw){
		BufferedImage result = new BufferedImage(image.getWidth()/size,image.getHeight()/size,BufferedImage.TYPE_INT_RGB);
		float[][] redchannel = null,greenchannel=null,bluechannel=null;
		if(bw){
			redchannel = GetChannel(image,REDCHANNEL,true);
			redchannel = Pool(redchannel,size,type_pooling);
		}else{
			if(rc){
				redchannel = GetChannel(image,REDCHANNEL,true);
				redchannel = Pool(redchannel,size,type_pooling);
			}
			if(gc){
				greenchannel = GetChannel(image,GREENCHANNEL,true);
				greenchannel = Pool(greenchannel,size,type_pooling);
			}
			if(bc){
				bluechannel = GetChannel(image,BLUECHANNEL,true);
				bluechannel = Pool(bluechannel,size,type_pooling);
			}
		}
		
		for(int x=0;x<result.getWidth();x++){
			for(int y=0;y<result.getHeight();y++){
				float redval=0,greenval=0,blueval=0;
				if(bw){
					redval = redchannel[x][y];
					greenval = redval;
					blueval = redval;
				}else{
					if(rc){
						redval = redchannel[x][y];
					}
					if(gc){
						greenval = greenchannel[x][y];
					}
					if(bc){
						blueval = bluechannel[x][y];
					}
				}
				Color c = new Color(
						Truncate(redval*255),
						Truncate(greenval*255),
						Truncate(blueval*255));
			
				result.setRGB(x, y,c.getRGB());
			}
		}
		return result;
	}
	public static float[][] Pool(float[][] data,int size,int type_pooling){
		float[][] result = new float[data.length/size][data[0].length/size];
		if(type_pooling == MAXPOOLING){
			for(int x=0;x<result.length;x++){
				for(int y=0;y<result[0].length;y++){
					result[x][y] = MaxPooling(x,y,data,size);
				}
			}
		}else if(type_pooling == MEANPOOLING){
			for(int x=0;x<result.length;x++){
				for(int y=0;y<result[0].length;y++){
					result[x][y] = MeanPooling(x,y,data,size);
				}
			}
		}else if(type_pooling == MINPOOLING){
			for(int x=0;x<result.length;x++){
				for(int y=0;y<result[0].length;y++){
					result[x][y] = MinPooling(x,y,data,size);
				}
			}
		}
		return result;
	}
	public static float MaxPooling(int xstart,int ystart,float[][] data,int size){
		float max = 0;
		for(int x=0;x<size;x++){
			for(int y=0;y<size;y++){
				if(data[(xstart*size)+x][(ystart*size)+y] > max){
					max = data[(xstart*size)+x][(ystart*size)+y];
				}
			}
		}
		return max;
	}
	public static float MinPooling(int xstart,int ystart,float[][] data,int size){
		float min = Float.MAX_VALUE;
		for(int x=0;x<size;x++){
			for(int y=0;y<size;y++){
				if(data[(xstart*size)+x][(ystart*size)+y] < min){
					min = data[(xstart*size)+x][(ystart*size)+y];
				}
			}
		}
		return min;
	}
	public static float MeanPooling(int xstart,int ystart,float[][] data,int size){
		float sum = 0;
		for(int x=0;x<size;x++){
			for(int y=0;y<size;y++){
				sum += data[(xstart*size)+x][(ystart*size)+y];
			}
		}
		return sum/(size*size);
	}
	public static BufferedImage ConvolveImage(BufferedImage image,Kernel kernel_obj,boolean rc,boolean gc,boolean bc,boolean bw){
		float[][] kernel = kernel_obj.kernel;
		float bias = kernel_obj.bias;
		float[][] redchannel = null,greenchannel=null,bluechannel=null;
		BufferedImage result = null;
		if(bw){
			redchannel = GetChannel(image,REDCHANNEL,true);
			redchannel = Convolution(redchannel,kernel,bias);
			result = new BufferedImage(redchannel.length,redchannel[0].length,BufferedImage.TYPE_INT_RGB);
		}else{
			if(rc){
				redchannel = GetChannel(image,REDCHANNEL,true);
				redchannel = Convolution(redchannel,kernel,bias);
				result = new BufferedImage(redchannel.length,redchannel[0].length,BufferedImage.TYPE_INT_RGB);
			}
			if(gc){
				greenchannel = GetChannel(image,GREENCHANNEL,true);
				greenchannel = Convolution(greenchannel,kernel,bias);
				result = new BufferedImage(greenchannel.length,greenchannel[0].length,BufferedImage.TYPE_INT_RGB);
			}
			if(bc){
				bluechannel = GetChannel(image,BLUECHANNEL,true);
				bluechannel = Convolution(bluechannel,kernel,bias);
				result = new BufferedImage(bluechannel.length,bluechannel[0].length,BufferedImage.TYPE_INT_RGB);
			}
		}
		
		
		for(int x=0;x<result.getWidth();x++){
			for(int y=0;y<result.getHeight();y++){
				float redval=0,greenval=0,blueval=0;
				if(bw){
					redval = redchannel[x][y];
					greenval = redval;
					blueval = redval;
				}else{
					if(rc){
						redval = redchannel[x][y];
					}
					if(gc){
						greenval = greenchannel[x][y];
					}
					if(bc){
						blueval = bluechannel[x][y];
					}
				}
				Color c = new Color(
						Truncate(redval*255),
						Truncate(greenval*255),
						Truncate(blueval*255));
				result.setRGB(x, y,c.getRGB());
			}
		}
		return result;
	}
	public static float[][] Convolution(float[][] data,float[][] kernel,float bias){
		float[][] result = new float[data.length-kernel.length+1][data[0].length-kernel.length+1];
		for(int x=0;x<data.length-kernel.length;x++){
			for(int y=0;y<data[x].length-kernel.length;y++){
				result[x][y] = DotKernel(x,y,data,kernel,bias);
			}
		}
		return result;
	}
	public static float DotKernel(int xstart,int ystart,float[][] data,float[][] kernel,float bias){
		float sum=0;
		for(int x=0;x<kernel.length;x++){
			for(int y=0;y<kernel.length;y++){
				sum += data[x+xstart][y+ystart]*kernel[x][y];
			}
		}
		return sum+bias;
	}
	public static BufferedImage Brightness(BufferedImage image,int brightness){
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				Color c = new Color(image.getRGB(x, y));
				Color newcolor = new Color(Truncate(c.getRed()+brightness), Truncate(c.getGreen()+brightness), Truncate(c.getBlue()+brightness));
				image.setRGB(x, y, newcolor.getRGB());
			}
		}
		return image;
	}
	public static BufferedImage Contrast(BufferedImage image,int contrast){
		
		float f = (float)(259*(255+contrast))/(255*(259-contrast));
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				Color c = new Color(image.getRGB(x, y));
				Color newcolor = new Color(
						Truncate(f*(c.getRed()-128)+128), 
						Truncate(f*(c.getGreen()-128)+128), 
						Truncate(f*(c.getBlue()-128)+128));
				image.setRGB(x, y, newcolor.getRGB());
			}
		}
		return image;
	}
}
