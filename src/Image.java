public class Image {
	public double[][] vector;
	/*public double [] v1;
	public double [] v2;
	public double [] v3;
	public double [] v4;*/
	
	public Image(){
		vector=new double[4][];
		for(int i=0;i<4;i++){
			vector[i]=new double[24];
		}
		/*v1=new double[24];
		v2=new double[24];
		v3=new double[24];
		v4=new double[24];	*/	
	}
	public double[] getvector(int i){
		return vector[i];
	}
	/*public double[] getv2(){
		return v2;
	}
	public double[] getv3(){
		return v3;
	}
	public double[] getv4(){
		return v4;
	}
*/
	public void setvector(int num,String s){
		
		String[] str=s.split(" ");
		for(int i=0;i<str.length;i++){
			vector[num][i]=Integer.parseInt(str[i]);
		}
	}
	/*public void setv2(String s){
		String[] str=s.split(" ");
		for(int i=0;i<str.length;i++){
			v2[i]=Integer.parseInt(str[i]);
		}
	}
	public void setv3(String s){
		String[] str=s.split(" ");
		for(int i=0;i<str.length;i++){
			v3[i]=Integer.parseInt(str[i]);
		}
	}
	public void setv4(String s){
		String[] str=s.split(" ");
		for(int i=0;i<str.length;i++){
			v4[i]=Integer.parseInt(str[i]);
		}
	}*/
	

}


