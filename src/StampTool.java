public class StampTool {
	
	public static String[] splitString(String s){
		String[] result = s.split(" ");
		return result;
	}
	//把字符串转为向量
	public static double[] toDouble(String[] s){
		double[] result = new double[24];
		for (int i = 0; i < s.length; i++){
			double r = Double.parseDouble(s[i]);
			result[i] = r;
		}
		return result;
	}

	//计算两个向量之间的欧式距离
	public static double getDistance(double[] vector1, double[] vector2){
		double sum = 0;
		for (int i = 0; i < vector2.length; i++) {
			double d1 = vector1[i];
			double d2 = vector2[i];
			sum += (d1-d2)*(d1-d2);
		}
		return Math.sqrt(sum);
	}
	
	public static void main(String[] args){
		System.out.println(Double.parseDouble("1.02e-02"));
		double[] vector1 = {1,2,3};
		double[] vector2 = {5,5,3};
		System.out.println(StampTool.getDistance(vector1, vector2));
		
	}
}
