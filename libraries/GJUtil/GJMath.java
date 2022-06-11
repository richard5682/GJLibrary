package GJUtil;

import java.awt.Point;
import java.awt.geom.Point2D;

public class GJMath {
	public static String GetFloatString(float val,int numberofdecimal){
		String format = "%."+numberofdecimal+"f";
		String returnval = String.format(format, val);
		return returnval;
	}
	public static boolean checkPointInside(Point p0,Point test,int width,int height){
		if(test.x > p0.x && test.x < p0.x+width){
			if(test.y > p0.y && test.y < p0.y+height){
				return true;
			}
		}
		return false;
	}
	public static class Vec2D{
		public float x;
		public float y;
		public Vec2D(float x,float y){
			this.x = x;
			this.y = y;
		}
	}
	public static float GetDistance(Point p1,Point p2) {
		float dx = p1.x-p2.x;
		float dy = p1.y-p2.y;
		return (float)Math.sqrt(dx*dx+dy*dy);
	}
	public static Point PointInterpolate(Point p1,Point p2,float t) {
		double x = p1.getX()*t + p2.getX()*(1-t);
		double y = p1.getY()*t + p2.getY()*(1-t);
		Point returnpoint = new Point(); 
		returnpoint.setLocation(x, y);
		return returnpoint;
	}
	public static Point Add(Point p1,Point p2){
		return new Point(p1.x+p2.x,p1.y+p2.y);
	}
	public static Point Subtract(Point p1,Point p2){
		return new Point(p1.x-p2.x,p1.y-p2.y);
	}
	public static Point GetLineIntersection(Point a1,Point b1,Point a2,Point b2){
		if(a1.x != b1.x && a2.x != b2.x){
			float m1 = (float)(a1.y-b1.y)/(a1.x-b1.x);
			float m2 = (float)(a2.y-b2.y)/(a2.x-b2.x);
			float yint1 = a1.y-(m1*a1.x);
			float yint2 = a2.y-(m2*a2.x);
			float rx = (yint2-yint1)/(m1-m2);
			float ry = m1*(rx)+yint1;
			return new Point((int)rx,(int)ry);
		}else if(a1.x == b1.x){
			float m2 = (float)(a2.y-b2.y)/(a2.x-b2.x);
			float yint2 = a2.y-(m2*a2.x);
			float rx = a1.x;
			float ry = m2*(rx)+yint2;
			return new Point((int)rx,(int)ry);
		}else if(a2.x == b2.x){
			float m1 = (float)(a1.y-b1.y)/(a1.x-b1.x);
			float yint1 = a1.y-(m1*a1.x);
			float rx = a2.x;
			float ry = m1*(rx)+yint1;
			return new Point((int)rx,(int)ry);
		}else{
			return null;
		}
	}
	public static boolean CheckOnMiddle(float x1,float x2,float x){
		float min,max;
		if(x1 >= x2){
			min = x2;
			max = x1;
		}else{
			min = x1;
			max = x2;
		}
		if(x >= min && x <= max){
			return true;
		}else{
			return false;
		}
	}
	public static boolean CheckLineIntersection(Point a1,Point b1,Point a2,Point b2){
		Point intersection = GetLineIntersection(a1,b1,a2,b2);
		if(intersection != null){
			if(CheckOnMiddle(a1.x,b1.x,intersection.x) && CheckOnMiddle(a2.x,b2.x,intersection.x)){
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
}
