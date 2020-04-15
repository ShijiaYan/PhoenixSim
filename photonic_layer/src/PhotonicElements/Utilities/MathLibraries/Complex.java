package PhotonicElements.Utilities.MathLibraries;

/******************************************************************************
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im fields after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 ******************************************************************************/

public class Complex {
    private final double re;   // the real part
    private final double im;   // the imaginary part

    public static final Complex ZERO = new Complex(0,0) ;
    public static final Complex ONE = new Complex(1,0) ;
    public static final Complex plusJ = new Complex(0,1) ;
    public static final Complex minusJ = new Complex(0,-1) ;

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

/*    // adding a default value for the Complex object
    public Complex(){
    	re = 0 ;
    	im = 0 ;
    }*/

    // return a string representation of the invoking Complex object
    public String toString() {
//        if (im == 0) return re + "";
//        if (re == 0) return "i" + im ;
        if (im <  0) return re + "-" + "i" + (-im) ;
        return re + "+" + "i" + im ;
    }

    // return abs/modulus/magnitude and angle/phase/argument
    public double abs() { return Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
    public double absSquared() {
    	return (Math.hypot(re, im)*Math.hypot(re, im)) ;
    }

    // between zero and 2*pi
    public double phase(){
    	double restrictedPhase = Math.atan2(im, re) ;
    	if (restrictedPhase>=0){
    		return restrictedPhase;
    	}
    	else{
    		return (restrictedPhase+2*Math.PI) ;
    	}
    }

    // between -pi and pi
    public double phaseMinusPiToPi() {
    	return Math.atan2(im, re);
    	}

    public double phaseDegree(){
    	return phase()*180/Math.PI ;
    }

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this + b)
    public Complex plus(double b) {
        Complex a = this;             // invoking object
        double real = a.re + b ;
        double imag = a.im + 0 ;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(double b) {
        Complex a = this;
        double real = a.re - b ;
        double imag = a.im - 0 ;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(re, -im); }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }

    // return a / b
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return complex (a/alpha) where a is complex and alpha is real
    public Complex divides(double alpha) {
        Complex a = this;
        return a.times(1/alpha);
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }

    // returns power of a complex number: Z^p where p is a real number
    public Complex pow(double p){
    	double magPowered = Math.pow(this.abs(), p) ;
    	double phasePowered = this.phase() * p ;
    	double real = magPowered * Math.cos(phasePowered) ;
    	double imag = magPowered * Math.sin(phasePowered) ;
    	Complex powResult = new Complex(real, imag) ;
    	return powResult ;
    }

    // equality of two complex numbers
    public boolean equals(Complex b){
    	Complex a = this ;
    	if(a.re()==b.re() & a.im()==b.im())
    		return true ;
    	else
    		return false ;
    }

    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    public Complex sqrt(){
    	Complex a = this ;
    	double real = Math.sqrt(a.abs()) * Math.cos(a.phase()/2) ;
    	double imag = Math.sqrt(a.abs()) * Math.sin(a.phase()/2) ;
    	return new Complex(real, imag) ;
    }

    public static boolean isNumeric(String st){
    	st = st.trim() ;
    	st = st.replaceAll("\\s+", "") ;
    	boolean testNull = st != null ;
    	boolean test1 = st.matches("[-+]?\\d*\\.?\\d*[+-]i?\\d*\\.?\\d*") ;
    	boolean test2 = st.matches("[-+]?\\d*\\.?\\d*[+-]?\\d*\\.?\\d*?i") ;
    	boolean test3 = st.matches("[-+]?\\d*\\.?\\d*[+-]j?\\d*\\.?\\d*") ;
    	boolean test4 = st.matches("[-+]?\\d*\\.?\\d*[+-]?\\d*\\.?\\d*?j") ;
    	boolean test5 = st.matches("[-+]?\\d*\\.?\\d*") ;
    	boolean test6 = st.matches("[-+]?i\\d*\\.?\\d*") ;
    	boolean test7 = st.matches("[-+]?j\\d*\\.?\\d*") ;
    	boolean test8 = st.matches("[-+]?\\d*\\.?\\d*i") ;
    	boolean test9 = st.matches("[-+]?\\d*\\.?\\d*j") ;
    	boolean result = testNull &&  (test1 || test2 || test3 || test4 || test5 || test6 || test7 || test8 || test9) ;
    	return result ;
    }

    public static Complex parseComplex(String st){
    	boolean hasJ = st.contains("j") ;
    	boolean hasI = st.contains("i") ;
    	st = st.trim() ;
    	st = st.replaceAll("\\s+", "") ;
    	if(isNumeric(st) && (hasI || hasJ)){
    		double realPart = 0 ;
    		double imagPart = 0 ;
    		String[] num = null ;
    		if(!hasJ){ num = st.split("i") ; } else{ num = st.split("j") ;}
			String real = num[0] ;
			String imag = num[1] ;
			// the correct format is "a + j b" or " a + i b"
			if(real.charAt(real.length()-1) == '+') {imagPart = MoreMath.evaluate(imag) ;}
			else{imagPart = -MoreMath.evaluate(imag) ; }
			if(real.charAt(0) == '-') {realPart = -MoreMath.evaluate(real.replaceAll("[+-]", "")) ; }
			else{realPart = MoreMath.evaluate(real.replaceAll("[+-]", "")) ; }
			return new Complex(realPart, imagPart) ;
    	}
    	else if(MoreMath.Conversions.Strings.isNumeric(st)){
    		return new Complex(MoreMath.evaluate(st), 0) ;
    	}
    	else {return null ;}
    }

    public static void main(String[] args) {
        Complex nu = new Complex(3, -2) ;
        System.out.println(nu);
        System.out.println(Complex.isNumeric(" 3 + i 2"));
        String st = "   2 + 3 i   " ;
        System.out.println("original: " + st);
        String stNoWhiteSpace = st.trim().replaceAll("\\s+", "") ;
        System.out.println("white space removed: " + stNoWhiteSpace);
        String st1 = "-2 - 3" ;
        System.out.println(st1.split("[+-]").length);
        System.out.println(st1.trim().split("[+-]")[0].equals(""));
        System.out.println(st1.trim().split("[+-]")[1]);
        System.out.println(st1.trim().split("[+-]")[2]);
        System.out.println("-----------------------------");
        System.out.println(parseComplex("+2.2   -  j 3 "));
//        System.out.println(parseComplex("+i2.2  "));
    }

}
