# Java 基础 

## ⼀、数据类型 

### 基本类型 

```java
byte/8 
char/16 
short/16 
int/32 
float/32 
long/64 
double/64 
boolean/~ 
```


​         boolean 只有两个值：true、false，可以使⽤ 1 bit 来存储，但是具体⼤⼩没有明确规定。JVM 会在编译时期将 boolean 类型的数据转换为 int，使⽤ 1 来表示 true，0 表示 false。JVM ⽀持 boolean 数组， 但是是通过读写 byte 数组来实现的。 



### 包装类型 

​		基本类型都有对应的包装类型，基本类型与其对应的包装类型之间的赋值使⽤⾃动装箱与拆箱完成。 

#### 缓存池 

new Integer(123) 与 Integer.valueOf(123) 的区别在于： 

<font color=red>new Integer(123) 每次都会新建⼀个对象；</font>

<font color=red>Integer.valueOf(123) 会使⽤缓存池中的对象，多次调⽤会取得同⼀个对象的引⽤。</font>

```java
		Integer x = 2; // 装箱 调⽤了 Integer.valueOf(2) 

​		int y = x; // 拆箱 调⽤了 X.intValue()

		
​		Integer x = new Integer(123); 

​		Integer y = new Integer(123); 

​		System.out.println(x == y); // false 

​		Integer z = Integer.valueOf(123); 

​		Integer k = Integer.valueOf(123); 

​		System.out.println(z == k); // true 
```

​		valueOf() ⽅法的实现⽐较简单，就是先判断值是否在缓存池中，如果在的话就直接返回缓存池的内容。(装箱操作) 

```java
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```

在 Java 8 中，Integer 缓存池的⼤⼩默认为 **-128~127**。 

```java
    private static class IntegerCache {
        static final int low = -128;
        static final int high;
        static final Integer cache[];

        static {
            // high value may be configured by property
            int h = 127;
            String integerCacheHighPropValue =
                sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
            if (integerCacheHighPropValue != null) {
                try {
                    int i = parseInt(integerCacheHighPropValue);
                    i = Math.max(i, 127);
                    // Maximum array size is Integer.MAX_VALUE
                    h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
                } catch( NumberFormatException nfe) {
                    // If the property cannot be parsed into an int, ignore it.
                }
            }
            high = h;

            cache = new Integer[(high - low) + 1];
            int j = low;
            for(int k = 0; k < cache.length; k++)
                cache[k] = new Integer(j++);

            // range [-128, 127] must be interned (JLS7 5.1.7)
            assert IntegerCache.high >= 127;
        }

        private IntegerCache() {}
    }
```

 		**<font color=red>编译器会在⾃动装箱过程调⽤ valueOf() ⽅法，因此多个值相同且值在缓存池范围内的 Integer 实例使⽤⾃动装箱来创建，那么就会引⽤相同的对象。</font>** 

基本类型对应的缓冲池如下： 

+ boolean values true and false 

+ all byte values 

+ short values between -128 and 127 

+ int values between -128 and 127 

+ char in the range \u0000 to \u007F 

​         在使⽤这些基本类型对应的包装类型时，如果该数值范围在缓冲池范围内，就可以直接使⽤缓冲池中的对象。 

​       <font color=red>  在 jdk 1.8 所有的数值类缓冲池中，Integer 的缓冲池 IntegerCache 很特殊，这个缓冲池的下界是 -128，上界默认是 127，但是这个上界是可调的，在启动 jvm 的时候，通过 -XX:AutoBoxCacheMax= <size> 来指定这个缓冲池的⼤⼩，该选项在 JVM 初始化的时候会设定⼀个名为 java.lang.IntegerCache.high 系统属性，然后 IntegerCache 初始化的时候就会读取该系统属性来决定上界。 </font>

StackOverflow : Differences between new Integer(123), Integer.valueOf(123) and just 123 

[https://stackoverflow.com/questions/9030817/differences-between-new-integer123-integer-valueof123-and-just-123]

## ⼆、**String** 

### 概览 

​		String 被声明为 final，因此它不可被继承。(Integer 等包装类也不能被继承） 

**在 Java 8 中，String 内部使⽤ char 数组存储数据。**

 ```java
public final class String
	implements java.io.Serializable, Comparable<String>, CharSequence {
	/** The value is used for character storage. */
	private final char value[];
}
 ```

​		在 Java 9 之后，String 类的实现改⽤ byte 数组存储字符串，同时使⽤ coder 来标识使⽤了哪种编码。 

```java
public final class String
	implements java.io.Serializable, Comparable<String>, CharSequence {
	/** The value is used for character storage. */
	private final byte[] value;
    
	/** The identifier of the encoding used to encode the bytes in 			{@codevalue}. */
	private final byte coder;
}
```

​		value 数组被声明为 final，这意味着 value 数组初始化之后就不能再引⽤其它数组。并且 String 内部没有改变 value 数组的⽅法，因此可以保证 String 不可变。 

### 不可变的好处 

**1.** 可以缓存 **hash** 值  

​		因为 String 的 hash 值经常被使⽤，例如 String ⽤做 HashMap 的 key。不可变的特性可以使得hash值也不可变，因此只需要进⾏⼀次计算。 

**2. String Pool** 的需要  

​		如果⼀个 String 对象已经被创建过了，那么就会从 String Pool 中取得引⽤。只有 String 是不可变的，才可能使⽤ String Pool。 

![1630810471345](C:\Users\Admin\AppData\Roaming\Typora\typora-user-images\1630810471345.png)

**3.** 安全性  

​		String 经常作为参数，String 不可变性可以保证参数不可变。例如在作为⽹络连接参数的情况下如果String 是可变的，那么在⽹络连接过程中，String 被改变，改变 String 的那⼀⽅以为现在连接的是其它主机，⽽实际情况却不⼀定是。 

**4.** 线程安全  

​		String 不可变性天⽣具备线程安全，可以在多个线程中安全地使⽤。 

### **String, StringBuffer and StringBuilder** 

**1.** 可变性  

+ String 不可变 

+ StringBuffer 和 StringBuilder 可变 

**2.** 线程安全  

+ String 不可变，因此是线程安全的 

+ StringBuilder 不是线程安全的 

+ StringBuffer 是线程安全的，内部使⽤ synchronized 进⾏同步 

### **String Pool** 

​		字符串常量池（String Pool）保存着所有字符串字⾯量（literal strings），这些字⾯量在编译时期就确定。不仅如此，**<font color=red>还可以使⽤ String 的 intern() ⽅法在运⾏过程将字符串添加到 String Pool 中</font>**。当⼀个字符串调⽤ intern() ⽅法时，如果 String Pool 中已经存在⼀个字符串和该字符串值相等（使⽤equals() ⽅法进⾏确定），那么就会返回 String Pool 中字符串的引⽤；否则，就会在 String Pool 中添加⼀个新的字符串，并返回这个新字符串的引⽤。 

​		下⾯示例中，s1 和 s2 采⽤ new String() 的⽅式新建了两个不同字符串，⽽ s3 和 s4 是通过 s1.intern() 和 s2.intern() ⽅法取得同⼀个字符串引⽤。intern() ⾸先把 "aaa" 放到String Pool 中，然后返回这个字符串引⽤，因此 s3 和 s4 引⽤的是同⼀个字符串。 

```java
String s1 = new String("aaa");
String s2 = new String("aaa");
System.out.println(s1 == s2); // false
String s3 = s1.intern();
String s4 = s2.intern();
System.out.println(s3 == s4); // true
```

​		如果是采⽤ "bbb" 这种字⾯量的形式创建字符串，会⾃动地将字符串放⼊ String Pool 中。

 ```java
String s5 = "bbb";
String s6 = "bbb";
System.out.println(s5 == s6); // true
 ```

​		在 Java 7 之前，String Pool 被放在运⾏时常量池中，它属于永久代。⽽在 Java 7，String Pool 被移到 堆中。这是因为永久代的空间有限，在⼤量使⽤字符串的场景下会导致 OutOfMemoryError 错误。 

 [深入解析String#intern](https://tech.meituan.com/2014/03/06/in-depth-understanding-string-intern.html)

### **new String("abc")** 

​		使⽤这种⽅式⼀共会创建两个字符串对象（前提是 String Pool 中还没有 "abc" 字符串对象）。 

+ "abc" 属于字符串字⾯量，因此编译时期会在 String Pool 中创建⼀个字符串对象，指向这个 "abc" 字符串字⾯量； 

+ ⽽使⽤ new 的⽅式会在堆中创建⼀个字符串对象。 

创建⼀个测试类，其 main ⽅法中使⽤这种⽅式来创建字符串对象。 

```java
public class NewStringTest {
	public static void main(String[] args) {
		String s = new String("abc");
	}
}
```

​		使⽤<font color=red>**javap -verbose**</font> 进⾏反编译，得到以下内容：

​		**通过<font color=red>javap -c</font>可查看具体的调用** 

```java
// ...
Constant pool:
// ...
	#2 = Class #18 // java/lang/String
	#3 = String #19 // abc
// ...
	#18 = Utf8 java/lang/String
	#19 = Utf8 abc
// ...
	public static void main(java.lang.String[]);
		descriptor: ([Ljava/lang/String;)V
		flags: ACC_PUBLIC, ACC_STATIC
		Code:
			stack=3, locals=2, args_size=1
				0: new #2 // class java/lang/String
				3: dup
				4: ldc #3 // String abc
				6: invokespecial #4 // Method java/lang/String."
<init>":(Ljava/lang/String;)V
				9: astore_1
// ...
```

​		在 Constant Pool 中，#19 存储这字符串字⾯量 "abc"，#3 是 String Pool 的字符串对象，它指向 #19这个字符串字⾯量。在 main ⽅法中，0: ⾏使⽤ new #2 在堆中创建⼀个字符串对象，并且使⽤ ldc #3 将 String Pool 中的字符串对象作为 String 构造函数的参数。 

​		以下是 String 构造函数的源码，可以看到，在将⼀个字符串对象作为另⼀个字符串对象的构造函数参数 时，并不会完全复制 value 数组内容，⽽是都会指向同⼀个 value 数组。

 ```java
public String(String original) {
	this.value = original.value;
	this.hash = original.hash;
}
 ```



## 三、运算 

### 参数传递 

​		Java 的参数是**以值传递**的形式传⼊⽅法中，⽽**不是引⽤传递**。 

​		以下代码中 Dog dog 的 dog 是⼀个指针，存储的是对象的地址。在将⼀个参数传⼊⼀个⽅法时，本质上是将**对象的地址以值的⽅式传递到形参中**。 

```java
public class Dog {
	String name;
	Dog(String name) {
		this.name = name;
	}
	String getName() {
		return this.name;
	}
	void setName(String name) {
		this.name = name;
	}
	String getObjectAddress() {
		return super.toString();
	}
}
```

​		在⽅法中改变对象的字段值会改变原对象该字段值，因为引⽤的是同⼀个对象。 

```java
class PassByValueExample {
	public static void main(String[] args) {
		Dog dog = new Dog("A");
		func(dog);
		System.out.println(dog.getName()); // B
	}
	private static void func(Dog dog) {
		dog.setName("B");
	}
}
```

​		但是在⽅法中将指针引⽤了其它对象，那么此时⽅法⾥和⽅法外的两个指针指向了不同的对象，在⼀个指针改变其所指向对象的内容对另⼀个指针所指向的对象没有影响。 

```java
public class PassByValueExample {
	public static void main(String[] args) {
		Dog dog = new Dog("A");
		System.out.println(dog.getObjectAddress()); // Dog@4554617c
		func(dog);
		System.out.println(dog.getObjectAddress()); // Dog@4554617c
		System.out.println(dog.getName()); // A
	}
	private static void func(Dog dog) {
		System.out.println(dog.getObjectAddress()); // Dog@4554617c
		dog = new Dog("B");
		System.out.println(dog.getObjectAddress()); // Dog@74a14482
		System.out.println(dog.getName()); // B
	}
}  
```



### **float** 与 **double** 

​		**Java 不能隐式执⾏向下转型，因为这会使得精度降低**。 

+ 1.1 字⾯量属于 double 类型，不能直接将 1.1 直接赋值给 float 变量，因为这是向下转型。 

```java
// float f = 1.1;
```

1.1f 字⾯量才是 float 类型。 

```java
float f = 1.1f;
```

### 隐式类型转换 

​		因为字⾯量 1 是 int 类型，它⽐ short 类型精度要⾼，因此不能隐式地将 int 类型向下转型为 short 类型。

```java
short s1 = 1;
// s1 = s1 + 1;
```

但是使⽤ += 或者 ++ 运算符会执⾏隐式类型转换。 

```java
s1 += 1;
s1++;
```

 上⾯的语句相当于将 s1 + 1 的计算结果进⾏了向下转型： 

```java
s1 = (short) (s1 + 1);
```

### **switch** 

​		从 Java 7 开始，**可以在 switch 条件判断语句中使⽤ String 对象**。 

```java
String s = "a";
switch (s) {
	case "a":
		System.out.println("aaa");
		break;
	case "b":
		System.out.println("bbb");
		break;
}
```

​		switch 不⽀持 long、float、double，是因为 switch 的设计初衷是对那些只有少数⼏个值的类型进⾏等值判断，如果值过于复杂，那么还是⽤ if ⽐较合适。 

## 四、关键字 

### **final** 

**1. 数据**  

​		声明数据为常量，可以是编译时常量，也可以是在运⾏时被初始化后不能被改变的常量。 

+ 对于基本类型，final 使数值不变； 

+ 对于引⽤类型，final 使引⽤不变，也就不能引⽤其它对象，但是被引⽤的对象本身是可以修改的。 

```java
final int x = 1;
// x = 2; // cannot assign value to final variable 'x'
final A y = new A();
y.a = 1;
```

**2.⽅法**  

​		<font color=red>**声明⽅法不能被⼦类重写。** </font>

​		private ⽅法隐式地被指定为 final，**如果在⼦类中定义的⽅法和基类中的⼀个 private ⽅法签名相同，此时⼦类的⽅法不是重写基类⽅法，⽽是在⼦类中定义了⼀个新的⽅法。** 

**3.** 类  

​		声明类不允许被继承。 

### **static** 

**1.** 静态变量  

+ 静态变量：⼜称为类变量，也就是说这个变量属于类的，类所有的实例都共享静态变量，可以直接通过类名来访问它。静态变量在内存中只存在⼀份。 

+ 实例变量：每创建⼀个实例就会产⽣⼀个实例变量，它与该实例同⽣共死。 

```java
public class A {
	private int x; // 实例例变量量
	private static int y; // 静态变量量
	public static void main(String[] args) {
		// int x = A.x; // Non-static field 'x' cannot be referenced from a static context
		A a = new A();
		int x = a.x;
		int y = A.y;
	}
}
```

**2.** 静态⽅法  

​		静态⽅法在类加载的时候就存在了，它不依赖于任何实例。所以静态⽅法必须有实现，也就是说它不能是抽象⽅法。 

```java
public abstract class A {
	public static void func1(){
	}
	// public abstract static void func2(); 
    // Illegal combination of modifiers: 'abstract' and 'static'
}
```

​		只能访问所属类的静态字段和静态⽅法，**⽅法中不能有 this 和 super 关键字，因此这两个关键字与具体对象关联**。 

```java
public class A {
	private static int x;
	private int y;
	public static void func1(){
		int a = x;
		// int b = y; // Non-static field 'y' cannot be referenced from a static context
		// int b = this.y; // 'A.this' cannot be referenced from a
static context
	}
}
```

**3.** 静态语句块  

​		静态语句块在**类初始化**时运⾏⼀次。 

```java
public class A {
	static {
		System.out.println("123");
	}
	public static void main(String[] args) {
		A a1 = new A();
		A a2 = new A();
	}
}
```

> 123

**4.** 静态内部类  

​		⾮静态内部类依赖于外部类的实例，也就是说需要先创建外部类实例，才能⽤这个实例去创建⾮静态内部类。⽽静态内部类不需要。 

```java
public class OuterClass {
	class InnerClass {
	}
	static class StaticInnerClass {
	}
	public static void main(String[] args) {
		// InnerClass innerClass = new InnerClass(); 
        // 'OuterClass.this'cannot be referenced from a static context
		OuterClass outerClass = new OuterClass();
		InnerClass innerClass = outerClass.new InnerClass();
		StaticInnerClass staticInnerClass = new StaticInnerClass();
	}
}
```

​		**静态内部类不能访问外部类的⾮静态的变量和⽅法。** 

**5.** 静态导包 

​		在使⽤静态变量和⽅法时不⽤再指明 ClassName，从⽽简化代码，但可读性⼤⼤降低。 

**6.** 初始化顺序  

​		**静态变量和静态语句块优先于实例变量和普通语句块**，静态变量和静态语句块的初始化顺序取决于它们在代码中的顺序。 

```java
public static String staticField = "静态变量";
```

```java
static {
	System.out.println("静态语句块");
}
```

```java
public String field = "实例例变量";
```

```java
{
	System.out.println("普通语句块");
}
```

最后才是构造函数的初始化。 

```java
public InitialOrderTest() {
	System.out.println("构造函数");
}
```

存在继承的情况下，初始化顺序为： 

+ ⽗类（静态变量、静态语句块） 

+ ⼦类（静态变量、静态语句块） 

+ ⽗类（实例变量、普通语句块） 

+ ⽗类（构造函数） 

+ ⼦类（实例变量、普通语句块） 

+ ⼦类（构造函数） 

## 五、**Object** 通⽤⽅法 

### 概览 

```java
public native int hashCode()
public boolean equals(Object obj)
protected native Object clone() throws CloneNotSupportedException
public String toString()
public final native Class<?> getClass()
protected void finalize() throws Throwable {}
public final native void notify()
public final native void notifyAll()
public final native void wait(long timeout) throws InterruptedException
public final void wait(long timeout, int nanos) throws InterruptedException
public final void wait() throws InterruptedException
```

### **equals()** 

**1.** 等价关系  

两个对象具有等价关系，需要满⾜以下五个条件： 

**Ⅰ ⾃反性**

```java
x.equals(x); // true
```

**Ⅱ 对称性** 

```java
x.equals(y) == y.equals(x); // true
```

**Ⅲ 传递性** 

```java
if (x.equals(y) && y.equals(z))
x.equals(z); // true;
```

**Ⅳ ⼀致性** 

多次调⽤ equals() ⽅法结果不变 

```java
x.equals(y) == x.equals(y); // true 
```

**Ⅴ 与 null 的⽐较** 

对任何不是 null 的对象 x 调⽤ x.equals(null) 结果都为 false 

```java
x.equals(null); // false; 
```

**2.** 等价与相等  

+ 对于基本类型，== 判断两个值是否相等，基本类型没有 equals() ⽅法。 

+ 对于引⽤类型，== 判断两个变量是否引⽤同⼀个对象，⽽ equals() 判断引⽤的对象是否等价。 

```java
Integer x = new Integer(1); 

Integer y = new Integer(1); 

System.out.println(x.equals(y)); // true 

System.out.println(x == y); // false 
```

**3.** 实现  

+ 检查是否为同⼀个对象的引⽤，如果是直接返回 true； 

+ 检查是否是同⼀个类型，如果不是，直接返回 false； 

+ 将 Object 对象进⾏转型； 

+ 判断每个关键域是否相等。 

```java
public class EqualExample { 
	private int x; 
	private int y; 
	private int z; 
	public EqualExample(int x, int y, int z) { 
		this.x = x; 
		this.y = y; 
		this.z = z; 
	} 

	@Override
	public boolean equals(Object o) { 
		if (this == o) return true; 
		if (o == null || getClass() != o.getClass()) return false; 
		EqualExample that = (EqualExample) o; 
		if (x != that.x) return false; 
		if (y != that.y) return false; 
		return z == that.z; 
	} 
} 


```

### **hashCode()** 

​		hashCode() 返回哈希值，⽽ equals() 是⽤来判断两个对象是否等价。**等价的两个对象散列值⼀定相同，但是散列值相同的两个对象不⼀定等价，这是因为计算哈希值具有随机性，两个值不同的对象可能计算出相同的哈希值。** 

​		在<font color=red>**覆盖 equals() ⽅法时应当总是覆盖 hashCode() ⽅法**</font>，保证等价的两个对象哈希值也相等。 

​		HashSet 和 HashMap 等集合类使⽤了 hashCode() ⽅法来计算对象应该存储的位置，因此要**将对象添加到这些集合类中，需要让对应的类实现 hashCode() ⽅法。** 

​		下⾯的代码中，新建了两个等价的对象，并将它们添加到 HashSet 中。我们希望将这两个对象当成⼀样的，只在集合中添加⼀个对象。但是 EqualExample 没有实现 hashCode() ⽅法，因此这两个对象的哈希值是不同的，最终导致集合添加了两个等价的对象。 

```java
EqualExample e1 = new EqualExample(1, 1, 1);
EqualExample e2 = new EqualExample(1, 1, 1);
System.out.println(e1.equals(e2)); // true
HashSet<EqualExample> set = new HashSet<>();
set.add(e1);
set.add(e2);
System.out.println(set.size()); // 2
```

​		理想的哈希函数应当具有均匀性，即不相等的对象应当均匀分布到所有可能的哈希值上。这就要求了哈希函数要把所有域的值都考虑进来。可以将每个域都当成 R 进制的某⼀位，然后组成⼀个 R 进制的整数。 

​		R ⼀般取 31，因为它是⼀个奇素数，如果是偶数的话，当出现乘法溢出，信息就会丢失，因为与 2 相乘相当于向左移⼀位，最左边的位丢失。并且⼀个数与 31 相乘可以转换成移位和减法： 31*x == (x<<5)-x ，编译器会⾃动进⾏这个优化。 

```java
@Override
public int hashCode() {
    int result = 17;
    result = 31 * result + x;
    result = 31 * result + y;
    result = 31 * result + z;
    return result;
}
```

### toString()	

​		默认返回 ToStringExample@4554617c 这种形式，其中 **@ 后⾯的数值为散列码的⽆符号⼗六进制表示。** 

**clone()** 

**1. cloneable**  

clone() 是 Object 的 protected ⽅法，它不是 public，⼀个类不显式去重写 clone()，其它类就不能直接 

去调⽤该类实例的 clone() ⽅法。 



public class CloneExample { 

 private int a; 

 private int b; 

}CloneExample e1 = new CloneExample(); 

// CloneExample e2 = e1.clone(); // 'clone()' has protected access in 

'java.lang.Object' 

重写 clone() 得到以下实现： 

public class CloneExample { 

 private int a; 

 private int b; 

 @Override 

 public CloneExample clone() throws CloneNotSupportedException { 

 return (CloneExample)super.clone(); 

 } 

} 

CloneExample e1 = new CloneExample(); 

try { 

 CloneExample e2 = e1.clone(); 

} catch (CloneNotSupportedException e) { 

 e.printStackTrace(); 

} 

java.lang.CloneNotSupportedException: CloneExample 

以上抛出了 CloneNotSupportedException，这是因为 CloneExample 没有实现 Cloneable 接⼝。 

应该注意的是，clone() ⽅法并不是 Cloneable 接⼝的⽅法，⽽是 Object 的⼀个 protected ⽅法。 

Cloneable 接⼝只是规定，如果⼀个类没有实现 Cloneable 接⼝⼜调⽤了 clone() ⽅法，就会抛出 

CloneNotSupportedException。 

public class CloneExample implements Cloneable { 

 private int a; 

 private int b; 

 @Override 

 public Object clone() throws CloneNotSupportedException { 

 return super.clone(); 

 } 

}**2.** 浅拷⻉  

拷⻉对象和原始对象的引⽤类型引⽤同⼀个对象。 

public class ShallowCloneExample implements Cloneable { 

 private int[] arr; 

 public ShallowCloneExample() { 

 arr = new int[10]; 

 for (int i = 0; i < arr.length; i++) { 

 arr[i] = i; 

 } 

 } 

 public void set(int index, int value) { 

 arr[index] = value; 

 } 

 public int get(int index) { 

 return arr[index]; 

 } 

 @Override 

 protected ShallowCloneExample clone() throws CloneNotSupportedException 

{ 

 return (ShallowCloneExample) super.clone(); 

 } 

} 

ShallowCloneExample e1 = new ShallowCloneExample(); 

ShallowCloneExample e2 = null; 

try { 

 e2 = e1.clone(); 

} catch (CloneNotSupportedException e) { 

 e.printStackTrace(); 

} 

e1.set(2, 222); 

System.out.println(e2.get(2)); // 222 

**3.** 深拷⻉  

拷⻉对象和原始对象的引⽤类型引⽤不同对象。public class DeepCloneExample implements Cloneable { 

 private int[] arr; 

 public DeepCloneExample() { 

 arr = new int[10]; 

 for (int i = 0; i < arr.length; i++) { 

 arr[i] = i; 

 } 

 } 

 public void set(int index, int value) { 

 arr[index] = value; 

 } 

 public int get(int index) { 

 return arr[index]; 

 } 

 @Override 

 protected DeepCloneExample clone() throws CloneNotSupportedException { 

 DeepCloneExample result = (DeepCloneExample) super.clone(); 

 result.arr = new int[arr.length]; 

 for (int i = 0; i < arr.length; i++) { 

 result.arr[i] = arr[i]; 

 } 

 return result; 

 } 

} 

DeepCloneExample e1 = new DeepCloneExample(); 

DeepCloneExample e2 = null; 

try { 

 e2 = e1.clone(); 

} catch (CloneNotSupportedException e) { 

 e.printStackTrace(); 

} 

e1.set(2, 222); 

System.out.println(e2.get(2)); // 2 

**4. clone()** 的替代⽅案 使⽤ clone() ⽅法来拷⻉⼀个对象即复杂⼜有⻛险，它会抛出异常，并且还需要类型转换。Effective 

Java 书上讲到，最好不要去使⽤ clone()，可以使⽤拷⻉构造函数或者拷⻉⼯⼚来拷⻉⼀个对象。 

六、继承 

访问权限 

Java 中有三个访问权限修饰符：private、protected 以及 public，如果不加访问修饰符，表示包级可 

⻅。 

可以对类或类中的成员（字段和⽅法）加上访问修饰符。 

public class CloneConstructorExample { 

 private int[] arr; 

 public CloneConstructorExample() { 

 arr = new int[10]; 

 for (int i = 0; i < arr.length; i++) { 

 arr[i] = i; 

 } 

 } 

 public CloneConstructorExample(CloneConstructorExample original) { 

 arr = new int[original.arr.length]; 

 for (int i = 0; i < original.arr.length; i++) { 

 arr[i] = original.arr[i]; 

 } 

 } 

 public void set(int index, int value) { 

 arr[index] = value; 

 } 

 public int get(int index) { 

 return arr[index]; 

 } 

} 

CloneConstructorExample e1 = new CloneConstructorExample(); 

CloneConstructorExample e2 = new CloneConstructorExample(e1); 

e1.set(2, 222); 

System.out.println(e2.get(2)); // 2类可⻅表示其它类可以⽤这个类创建实例对象。 

成员可⻅表示其它类可以⽤这个类的实例对象访问到该成员； 

protected ⽤于修饰成员，表示在继承体系中成员对于⼦类可⻅，但是这个访问修饰符对于类没有意 

义。 

设计良好的模块会隐藏所有的实现细节，把它的 API 与它的实现清晰地隔离开来。模块之间只通过它们 

的 API 进⾏通信，⼀个模块不需要知道其他模块的内部⼯作情况，这个概念被称为信息隐藏或封装。因 

此访问权限应当尽可能地使每个类或者成员不被外界访问。 

如果⼦类的⽅法重写了⽗类的⽅法，那么⼦类中该⽅法的访问级别不允许低于⽗类的访问级别。这是为 

了确保可以使⽤⽗类实例的地⽅都可以使⽤⼦类实例去代替，也就是确保满⾜⾥⽒替换原则。 

字段决不能是公有的，因为这么做的话就失去了对这个字段修改⾏为的控制，客户端可以对其随意修 

改。例如下⾯的例⼦中，AccessExample 拥有 id 公有字段，如果在某个时刻，我们想要使⽤ int 存储 

id 字段，那么就需要修改所有的客户端代码。 

public class AccessExample { 

 public String id; 

} 

可以使⽤公有的 getter 和 setter ⽅法来替换公有字段，这样的话就可以控制对字段的修改⾏为。 

public class AccessExample { 

 private int id; 

 public String getId() { 

 return id + ""; 

 } 

 public void setId(String id) { 

 this.id = Integer.valueOf(id); 

 } 

} 

但是也有例外，如果是包级私有的类或者私有的嵌套类，那么直接暴露成员不会有特别⼤的影响。 

public class AccessWithInnerClassExample { 

 private class InnerClass { 

 int x; 

 }抽象类与接⼝ 

**1.** 抽象类  

抽象类和抽象⽅法都使⽤ abstract 关键字进⾏声明。如果⼀个类中包含抽象⽅法，那么这个类必须声明 

为抽象类。 

抽象类和普通类最⼤的区别是，抽象类不能被实例化，只能被继承。 

 private InnerClass innerClass; 

 public AccessWithInnerClassExample() { 

 innerClass = new InnerClass(); 

 } 

 public int getValue() { 

 return innerClass.x; // 直接访问 

 } 

} 

public abstract class AbstractClassExample { 

 protected int x; 

 private int y; 

 public abstract void func1(); 

 public void func2() { 

 System.out.println("func2"); 

 } 

} 

public class AbstractExtendClassExample extends AbstractClassExample { 

 @Override 

 public void func1() { 

 System.out.println("func1"); 

 } 

}// AbstractClassExample ac1 = new AbstractClassExample(); // 

'AbstractClassExample' is abstract; cannot be instantiated 

AbstractClassExample ac2 = new AbstractExtendClassExample(); 

ac2.func1(); 

**2.** 接⼝  

接⼝是抽象类的延伸，在 Java 8 之前，它可以看成是⼀个完全抽象的类，也就是说它不能有任何的⽅ 

法实现。 

从 Java 8 开始，接⼝也可以拥有默认的⽅法实现，这是因为不⽀持默认⽅法的接⼝的维护成本太⾼ 

了。在 Java 8 之前，如果⼀个接⼝想要添加新的⽅法，那么要修改所有实现了该接⼝的类，让它们都 

实现新增的⽅法。 

接⼝的成员（字段 + ⽅法）默认都是 public 的，并且不允许定义为 private 或者 protected。从 Java 9 

开始，允许将⽅法定义为 private，这样就能定义某些复⽤的代码⼜不会把⽅法暴露出去。 

接⼝的字段默认都是 static 和 final 的。 

public interface InterfaceExample { 

 void func1(); 

 default void func2(){ 

 System.out.println("func2"); 

 } 

 int x = 123; 

 // int y; // Variable 'y' might not have been initialized 

 public int z = 0; // Modifier 'public' is redundant for interface 

fields 

 // private int k = 0; // Modifier 'private' not allowed here 

 // protected int l = 0; // Modifier 'protected' not allowed here 

 // private void fun3(); // Modifier 'private' not allowed here 

} 

public class InterfaceImplementExample implements InterfaceExample { 

 @Override 

 public void func1() { 

 System.out.println("func1"); 

 } 

}**3.** ⽐较  

从设计层⾯上看，抽象类提供了⼀种 IS-A 关系，需要满⾜⾥式替换原则，即⼦类对象必须能够替换 

掉所有⽗类对象。⽽接⼝更像是⼀种 LIKE-A 关系，它只是提供⼀种⽅法实现契约，并不要求接⼝ 

和实现接⼝的类具有 IS-A 关系。 

从使⽤上来看，⼀个类可以实现多个接⼝，但是不能继承多个抽象类。 

接⼝的字段只能是 static 和 final 类型的，⽽抽象类的字段没有这种限制。 

接⼝的成员只能是 public 的，⽽抽象类的成员可以有多种访问权限。 

**4.** 使⽤选择  

使⽤接⼝： 

需要让不相关的类都实现⼀个⽅法，例如不相关的类都可以实现 Comparable 接⼝中的 

compareTo() ⽅法； 

需要使⽤多重继承。 

使⽤抽象类： 

需要在⼏个相关的类中共享代码。 

需要能控制继承来的成员的访问权限，⽽不是都为 public。 

需要继承⾮静态和⾮常量字段。 

在很多情况下，接⼝优先于抽象类。因为接⼝没有抽象类严格的类层次结构要求，可以灵活地为⼀个类 

添加⾏为。并且从 Java 8 开始，接⼝也可以有默认的⽅法实现，使得修改接⼝的成本也变的很低。 

Abstract Methods and Classes 

深⼊理解 abstract class 和 interface 

When to Use Abstract Class and Interface 

Java 9 Private Methods in Interfaces 

**super** 

访问⽗类的构造函数：可以使⽤ super() 函数访问⽗类的构造函数，从⽽委托⽗类完成⼀些初始化 

的⼯作。应该注意到，⼦类⼀定会调⽤⽗类的构造函数来完成初始化⼯作，⼀般是调⽤⽗类的默认 

构造函数，如果⼦类需要调⽤⽗类其它构造函数，那么就可以使⽤ super() 函数。 

访问⽗类的成员：如果⼦类重写了⽗类的某个⽅法，可以通过使⽤ super 关键字来引⽤⽗类的⽅法 

实现。 

// InterfaceExample ie1 = new InterfaceExample(); // 'InterfaceExample' is 

abstract; cannot be instantiated 

InterfaceExample ie2 = new InterfaceImplementExample(); 

ie2.func1(); 

System.out.println(InterfaceExample.x);Using the Keyword super 

重写与重载 

public class SuperExample { 

 protected int x; 

 protected int y; 

 public SuperExample(int x, int y) { 

 this.x = x; 

 this.y = y; 

 } 

 public void func() { 

 System.out.println("SuperExample.func()"); 

 } 

} 

public class SuperExtendExample extends SuperExample { 

 private int z; 

 public SuperExtendExample(int x, int y, int z) { 

 super(x, y); 

 this.z = z; 

 } 

 @Override 

 public void func() { 

 super.func(); 

 System.out.println("SuperExtendExample.func()"); 

 } 

} 

SuperExample e = new SuperExtendExample(1, 2, 3); 

e.func(); 

SuperExample.func() 

SuperExtendExample.func()**1.** 重写（**Override**）  

存在于继承体系中，指⼦类实现了⼀个与⽗类在⽅法声明上完全相同的⼀个⽅法。 

为了满⾜⾥式替换原则，重写有以下三个限制： 

⼦类⽅法的访问权限必须⼤于等于⽗类⽅法； 

⼦类⽅法的返回类型必须是⽗类⽅法返回类型或为其⼦类型。 

⼦类⽅法抛出的异常类型必须是⽗类抛出异常类型或为其⼦类型。 

使⽤ @Override 注解，可以让编译器帮忙检查是否满⾜上⾯的三个限制条件。 

下⾯的示例中，SubClass 为 SuperClass 的⼦类，SubClass 重写了 SuperClass 的 func() ⽅法。其 

中： 

⼦类⽅法访问权限为 public，⼤于⽗类的 protected。 

⼦类的返回类型为 ArrayList<Integer>，是⽗类返回类型 List<Integer> 的⼦类。 

⼦类抛出的异常类型为 Exception，是⽗类抛出异常 Throwable 的⼦类。 

⼦类重写⽅法使⽤ @Override 注解，从⽽让编译器⾃动检查是否满⾜限制条件。 

class SuperClass { 

 protected List<Integer> func() throws Throwable { 

 return new ArrayList<>(); 

 } 

} 

class SubClass extends SuperClass { 

 @Override 

 public ArrayList<Integer> func() throws Exception { 

 return new ArrayList<>(); 

 } 

} 

在调⽤⼀个⽅法时，先从本类中查找看是否有对应的⽅法，如果没有再到⽗类中查看，看是否从⽗类继 

承来。否则就要对参数进⾏转型，转成⽗类之后看是否有对应的⽅法。总的来说，⽅法调⽤的优先级 

为： 

this.func(this) 

super.func(this) 

this.func(super) 

super.func(super) 

/* 

 A | 

 B 

 | 

 C 

 | 

 D 

*/ 

class A { 

 public void show(A obj) { 

 System.out.println("A.show(A)"); 

 } 

 public void show(C obj) { 

 System.out.println("A.show(C)"); 

 } 

} 

class B extends A { 

 @Override 

 public void show(A obj) { 

 System.out.println("B.show(A)"); 

 } 

} 

class C extends B { 

} 

class D extends C { 

} 

public static void main(String[] args) { 

 A a = new A(); 

 B b = new B(); 

 C c = new C(); 

 D d = new D(); 

 // 在 A 中存在 show(A obj)，直接调⽤ 

 a.show(a); // A.show(A) 

 // 在 A 中不存在 show(B obj)，将 B 转型成其⽗类 A**2.** 重载（**Overload**）  

存在于同⼀个类中，指⼀个⽅法与已经存在的⽅法名称上相同，但是参数类型、个数、顺序⾄少有⼀个 

不同。 

应该注意的是，返回值不同，其它都相同不算是重载。 

## 七、反射 

每个类都有⼀个 **Class** 对象，包含了与类有关的信息。当编译⼀个新类时，会产⽣⼀个同名的 .class 

⽂件，该⽂件内容保存着 Class 对象。 

 a.show(b); // A.show(A) 

 // 在 B 中存在从 A 继承来的 show(C obj)，直接调⽤ 

 b.show(c); // A.show(C) 

 // 在 B 中不存在 show(D obj)，但是存在从 A 继承来的 show(C obj)，将 D 转型成其 

⽗类 C 

 b.show(d); // A.show(C) 

 // 引⽤的还是 B 对象，所以 ba 和 b 的调⽤结果⼀样 

 A ba = new B(); 

 ba.show(c); // A.show(C) 

 ba.show(d); // A.show(C) 

} 

class OverloadingExample { 

 public void show(int x) { 

 System.out.println(x); 

 } 

 public void show(int x, String y) { 

 System.out.println(x + " " + y); 

 } 

} 

public static void main(String[] args) { 

 OverloadingExample example = new OverloadingExample(); 

 example.show(1); 

 example.show(1, "2"); 

}类加载相当于 Class 对象的加载，类在第⼀次使⽤时才动态加载到 JVM 中。也可以使⽤ 

Class.forName("com.mysql.jdbc.Driver") 这种⽅式来控制类的加载，该⽅法会返回⼀个 Class 对 

象。 

反射可以提供运⾏时的类信息，并且这个类可以在运⾏时才加载进来，甚⾄在编译时期该类的 .class 不 

存在也可以加载进来。 

Class 和 java.lang.reflect ⼀起对反射提供了⽀持，java.lang.reflect 类库主要包含了以下三个类： 

**Field** ：可以使⽤ get() 和 set() ⽅法读取和修改 Field 对象关联的字段； 

**Method** ：可以使⽤ invoke() ⽅法调⽤与 Method 对象关联的⽅法； 

**Constructor** ：可以⽤ Constructor 的 newInstance() 创建新的对象。 

反射的优点：  

可扩展性 ：应⽤程序可以利⽤全限定名创建可扩展对象的实例，来使⽤来⾃外部的⽤户⾃定义 

类。 

类浏览器和可视化开发环境 ：⼀个类浏览器需要可以枚举类的成员。可视化开发环境（如 IDE） 

可以从利⽤反射中可⽤的类型信息中受益，以帮助程序员编写正确的代码。 

调试器和测试⼯具 ： 调试器需要能够检查⼀个类⾥的私有成员。测试⼯具可以利⽤反射来⾃动地 

调⽤类⾥定义的可被发现的 API 定义，以确保⼀组测试中有较⾼的代码覆盖率。 

反射的缺点：  

尽管反射⾮常强⼤，但也不能滥⽤。如果⼀个功能可以不⽤反射完成，那么最好就不⽤。在我们使⽤反 

射技术时，下⾯⼏条内容应该牢记于⼼。 

性能开销 ：反射涉及了动态类型的解析，所以 JVM ⽆法对这些代码进⾏优化。因此，反射操作的 

效率要⽐那些⾮反射操作低得多。我们应该避免在经常被执⾏的代码或对性能要求很⾼的程序中使 

⽤反射。 

安全限制 ：使⽤反射技术要求程序必须在⼀个没有安全限制的环境中运⾏。如果⼀个程序必须在 

有安全限制的环境中运⾏，如 Applet，那么这就是个问题了。 

内部暴露 ：由于反射允许代码执⾏⼀些在正常情况下不被允许的操作（⽐如访问私有的属性和⽅ 

法），所以使⽤反射可能会导致意料之外的副作⽤，这可能导致代码功能失调并破坏可移植性。反 

射代码破坏了抽象性，因此当平台发⽣改变的时候，代码的⾏为就有可能也随着变化。 

Trail: The Reflection API 

深⼊解析 Java 反射（1）- 基础 

## ⼋、异常 

Throwable 可以⽤来表示任何可以作为异常抛出的类，分为两种： **Error** 和 **Exception**。其中 Error 

⽤来表示 JVM ⽆法处理的错误，Exception 分为两种： 

受检异常 ：需要⽤ try...catch... 语句捕获并进⾏处理，并且可以从异常中恢复； 

⾮受检异常 ：是程序运⾏时错误，例如除 0 会引发 Arithmetic Exception，此时程序崩溃并且⽆法恢复。 

Java ⼊⻔之异常处理 

Java Exception Interview Questions and Answers 

九、泛型 

Java 泛型详解 

10 道 Java 泛型⾯试题 

⼗、注解 

Java 注解是附加在代码中的⼀些元信息，⽤于⼀些⼯具在编译、运⾏时进⾏解析和使⽤，起到说明、配 

置的功能。注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作⽤。 

注解 Annotation 实现原理与⾃定义注解例⼦ 

⼗⼀、特性 

**Java** 各版本的新特性 

public class Box<T> { 

 // T stands for "Type" 

 private T t; 

 public void set(T t) { this.t = t; } 

 public T get() { return t; } 

}**New highlights in Java SE 8**  

\1. Lambda Expressions 

\2. Pipelines and Streams 

\3. Date and Time API 

\4. Default Methods 

\5. Type Annotations 

\6. Nashhorn JavaScript Engine 

\7. Concurrent Accumulators 

\8. Parallel operations 

\9. PermGen Error Removed 

**New highlights in Java SE 7**  

\1. Strings in Switch Statement 

\2. Type Inference for Generic Instance Creation 

\3. Multiple Exception Handling 

\4. Support for Dynamic Languages 

\5. Try with Resources 

\6. Java nio Package 

\7. Binary Literals, Underscore in literals 

\8. Diamond Syntax 

Difference between Java 1.8 and Java 1.7? 

Java 8 特性 

**Java** 与 **C++** 的区别 

Java 是纯粹的⾯向对象语⾔，所有的对象都继承⾃ java.lang.Object，C++ 为了兼容 C 即⽀持⾯向 

对象也⽀持⾯向过程。 

Java 通过虚拟机从⽽实现跨平台特性，但是 C++ 依赖于特定的平台。 

Java 没有指针，它的引⽤可以理解为安全指针，⽽ C++ 具有和 C ⼀样的指针。 

Java ⽀持⾃动垃圾回收，⽽ C++ 需要⼿动回收。 

Java 不⽀持多重继承，只能通过实现多个接⼝来达到相同⽬的，⽽ C++ ⽀持多重继承。 

Java 不⽀持操作符重载，虽然可以对两个 String 对象执⾏加法运算，但是这是语⾔内置⽀持的操 

作，不属于操作符重载，⽽ C++ 可以。 

Java 的 goto 是保留字，但是不可⽤，C++ 可以使⽤ goto。 

What are the main differences between Java and C++? 

**JRE or JDK** 

JRE：Java Runtime Environment，Java 运⾏环境的简称，为 Java 的运⾏提供了所需的环境。它 

是⼀个 JVM 程序，主要包括了 JVM 的标准实现和⼀些 Java 基本类库。 

JDK：Java Development Kit，Java 开发⼯具包，提供了 Java 的开发及运⾏环境。JDK 是 Java 开发的核⼼，集成了 JRE 以及⼀些其它的⼯具，⽐如编译 Java 源码的编译器 javac 等。 

参考资料 

Eckel B. Java 编程思想[M]. 机械⼯业出版社, 2002. 

Bloch J. Effective java[M]. Addison-Wesley Professional, 2017. 

Java 容器 

Java 容器 

⼀、概览 

Collection 

Map 

⼆、容器中的设计模式 

迭代器模式 

适配器模式 

三、源码分析 

ArrayList 

Vector 

CopyOnWriteArrayList 

LinkedList 

HashMap 

ConcurrentHashMap 

LinkedHashMap 

WeakHashMap 

参考资料 

⼀、概览 

容器主要包括 Collection 和 Map 两种，Collection 存储着对象的集合，⽽ Map 存储着键值对（两个对 

象）的映射表。 

**Collection**1. Set 

TreeSet：基于红⿊树实现，⽀持有序性操作，例如根据⼀个范围查找元素的操作。但是查找效率不 

如 HashSet，HashSet 查找的时间复杂度为 O(1)，TreeSet 则为 O(logN)。 

HashSet：基于哈希表实现，⽀持快速查找，但不⽀持有序性操作。并且失去了元素的插⼊顺序信 

息，也就是说使⽤ Iterator 遍历 HashSet 得到的结果是不确定的。 

LinkedHashSet：具有 HashSet 的查找效率，并且内部使⽤双向链表维护元素的插⼊顺序。 

\2. List 

ArrayList：基于动态数组实现，⽀持随机访问。 

Vector：和 ArrayList 类似，但它是线程安全的。 

LinkedList：基于双向链表实现，只能顺序访问，但是可以快速地在链表中间插⼊和删除元素。不 

仅如此，LinkedList 还可以⽤作栈、队列和双向队列。 

\3. Queue 

LinkedList：可以⽤它来实现双向队列。 

PriorityQueue：基于堆结构实现，可以⽤它来实现优先队列。 

**Map**TreeMap：基于红⿊树实现。 

HashMap：基于哈希表实现。 

HashTable：和 HashMap 类似，但它是线程安全的，这意味着同⼀时刻多个线程同时写⼊ 

HashTable 不会导致数据不⼀致。它是遗留类，不应该去使⽤它，⽽是使⽤ ConcurrentHashMap 

来⽀持线程安全，ConcurrentHashMap 的效率会更⾼，因为 ConcurrentHashMap 引⼊了分段锁。 

LinkedHashMap：使⽤双向链表来维护元素的顺序，顺序为插⼊顺序或者最近最少使⽤（LRU）顺 

序。 

⼆、容器中的设计模式 

迭代器模式Collection 继承了 Iterable 接⼝，其中的 iterator() ⽅法能够产⽣⼀个 Iterator 对象，通过这个对象就可 

以迭代遍历 Collection 中的元素。 

从 JDK 1.5 之后可以使⽤ foreach ⽅法来遍历实现了 Iterable 接⼝的聚合对象。 

适配器模式 

java.util.Arrays#asList() 可以把数组类型转换为 List 类型。 

应该注意的是 asList() 的参数为泛型的变⻓参数，不能使⽤基本类型数组作为参数，只能使⽤相应的包 

装类型数组。 

也可以使⽤以下⽅式调⽤ asList()： 

三、源码分析 

如果没有特别说明，以下源码分析基于 JDK 1.8。 

在 IDEA 中 double shift 调出 Search EveryWhere，查找源码⽂件，找到之后就可以阅读源码。 

**ArrayList** 

\1. 概览 

因为 ArrayList 是基于数组实现的，所以⽀持快速随机访问。RandomAccess 接⼝标识着该类⽀持快速 

随机访问。 

List<String> list = new ArrayList<>(); 

list.add("a"); 

list.add("b"); 

for (String item : list) { 

 System.out.println(item); 

} 

@SafeVarargs 

public static <T> List<T> asList(T... a) 

Integer[] arr = {1, 2, 3}; 

List list = Arrays.asList(arr); 

List list = Arrays.asList(1, 2, 3);数组的默认⼤⼩为 10。 

\2. 扩容 

添加元素时使⽤ ensureCapacityInternal() ⽅法来保证容量⾜够，如果不够时，需要使⽤ grow() ⽅法进 

⾏扩容，新容量的⼤⼩为 oldCapacity + (oldCapacity >> 1) ，即 oldCapacity+oldCapacity/2。其 

中 oldCapacity >> 1 需要取整，所以新容量⼤约是旧容量的 1.5 倍左右。（oldCapacity 为偶数就是 

1.5 倍，为奇数就是 1.5 倍-0.5） 

扩容操作需要调⽤ Arrays.copyOf() 把原数组整个复制到新数组中，这个操作代价很⾼，因此最好在 

创建 ArrayList 对象时就指定⼤概的容量⼤⼩，减少扩容操作的次数。 

public class ArrayList<E> extends AbstractList<E> 

 implements List<E>, RandomAccess, Cloneable, java.io.Serializable 

private static final int DEFAULT_CAPACITY = 10; 

public boolean add(E e) { 

 ensureCapacityInternal(size + 1); // Increments modCount!! 

 elementData[size++] = e; 

 return true; 

} 

private void ensureCapacityInternal(int minCapacity) { 

 if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) { 

 minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity); 

 } 

 ensureExplicitCapacity(minCapacity); 

} 

private void ensureExplicitCapacity(int minCapacity) { 

 modCount++; 

 // overflow-conscious code 

 if (minCapacity - elementData.length > 0)3. 删除元素 

需要调⽤ System.arraycopy() 将 index+1 后⾯的元素都复制到 index 位置上，该操作的时间复杂度为 

O(N)，可以看到 ArrayList 删除元素的代价是⾮常⾼的。 

\4. 序列化 

ArrayList 基于数组实现，并且具有动态扩容特性，因此保存元素的数组不⼀定都会被使⽤，那么就没必 

要全部进⾏序列化。 

保存元素的数组 elementData 使⽤ transient 修饰，该关键字声明数组默认不会被序列化。 

ArrayList 实现了 writeObject() 和 readObject() 来控制只序列化数组中有元素填充那部分内容。 

 grow(minCapacity); 

} 

private void grow(int minCapacity) { 

 // overflow-conscious code 

 int oldCapacity = elementData.length; 

 int newCapacity = oldCapacity + (oldCapacity >> 1); 

 if (newCapacity - minCapacity < 0) 

 newCapacity = minCapacity; 

 if (newCapacity - MAX_ARRAY_SIZE > 0) 

 newCapacity = hugeCapacity(minCapacity); 

 // minCapacity is usually close to size, so this is a win: 

 elementData = Arrays.copyOf(elementData, newCapacity); 

} 

public E remove(int index) { 

 rangeCheck(index); 

 modCount++; 

 E oldValue = elementData(index); 

 int numMoved = size - index - 1; 

 if (numMoved > 0) 

 System.arraycopy(elementData, index+1, elementData, index, 

numMoved); 

 elementData[--size] = null; // clear to let GC do its work 

 return oldValue; 

} 

transient Object[] elementData; // non-private to simplify nested class 

accessprivate void readObject(java.io.ObjectInputStream s) 

 throws java.io.IOException, ClassNotFoundException { 

 elementData = EMPTY_ELEMENTDATA; 

 // Read in size, and any hidden stuff 

 s.defaultReadObject(); 

 // Read in capacity 

 s.readInt(); // ignored 

 if (size > 0) { 

 // be like clone(), allocate array based upon size not capacity 

 ensureCapacityInternal(size); 

 Object[] a = elementData; 

 // Read in all elements in the proper order. 

 for (int i=0; i<size; i++) { 

 a[i] = s.readObject(); 

 } 

 } 

} 

private void writeObject(java.io.ObjectOutputStream s) 

 throws java.io.IOException{ 

 // Write out element count, and any hidden stuff 

 int expectedModCount = modCount; 

 s.defaultWriteObject(); 

 // Write out size as capacity for behavioural compatibility with 

clone() 

 s.writeInt(size); 

 // Write out all elements in the proper order. 

 for (int i=0; i<size; i++) { 

 s.writeObject(elementData[i]); 

 } 

 if (modCount != expectedModCount) { 

 throw new ConcurrentModificationException(); 

 } 

}序列化时需要使⽤ ObjectOutputStream 的 writeObject() 将对象转换为字节流并输出。⽽ writeObject() 

⽅法在传⼊的对象存在 writeObject() 的时候会去反射调⽤该对象的 writeObject() 来实现序列化。反序 

列化使⽤的是 ObjectInputStream 的 readObject() ⽅法，原理类似。 

\5. Fail-Fast 

modCount ⽤来记录 ArrayList 结构发⽣变化的次数。结构发⽣变化是指添加或者删除⾄少⼀个元素的 

所有操作，或者是调整内部数组的⼤⼩，仅仅只是设置元素的值不算结构发⽣变化。 

在进⾏序列化或者迭代等操作时，需要⽐较操作前后 modCount 是否改变，如果改变了需要抛出 

ConcurrentModificationException。代码参考上节序列化中的 writeObject() ⽅法。 

**Vector** 

\1. 同步 

它的实现与 ArrayList 类似，但是使⽤了 synchronized 进⾏同步。 

\2. 扩容 

Vector 的构造函数可以传⼊ capacityIncrement 参数，它的作⽤是在扩容时使容量 capacity 增⻓ 

capacityIncrement。如果这个参数的值⼩于等于 0，扩容时每次都令 capacity 为原来的两倍。 

ArrayList list = new ArrayList(); 

ObjectOutputStream oos = new ObjectOutputStream(new 

FileOutputStream(file)); 

oos.writeObject(list); 

public synchronized boolean add(E e) { 

 modCount++; 

 ensureCapacityHelper(elementCount + 1); 

 elementData[elementCount++] = e; 

 return true; 

} 

public synchronized E get(int index) { 

 if (index >= elementCount) 

 throw new ArrayIndexOutOfBoundsException(index); 

 return elementData(index); 

}调⽤没有 capacityIncrement 的构造函数时，capacityIncrement 值被设置为 0，也就是说默认情况下 

Vector 每次扩容时容量都会翻倍。 

\3. 与 ArrayList 的⽐较 

Vector 是同步的，因此开销就⽐ ArrayList 要⼤，访问速度更慢。最好使⽤ ArrayList ⽽不是 

Vector，因为同步操作完全可以由程序员⾃⼰来控制； 

Vector 每次扩容请求其⼤⼩的 2 倍（也可以通过构造函数设置增⻓的容量），⽽ ArrayList 是 1.5 

倍。 

\4. 替代⽅案 

可以使⽤ Collections.synchronizedList(); 得到⼀个线程安全的 ArrayList。 

public Vector(int initialCapacity, int capacityIncrement) { 

 super(); 

 if (initialCapacity < 0) 

 throw new IllegalArgumentException("Illegal Capacity: "+ 

 initialCapacity); 

 this.elementData = new Object[initialCapacity]; 

 this.capacityIncrement = capacityIncrement; 

} 

private void grow(int minCapacity) { 

 // overflow-conscious code 

 int oldCapacity = elementData.length; 

 int newCapacity = oldCapacity + ((capacityIncrement > 0) ? 

 capacityIncrement : oldCapacity); 

 if (newCapacity - minCapacity < 0) 

 newCapacity = minCapacity; 

 if (newCapacity - MAX_ARRAY_SIZE > 0) 

 newCapacity = hugeCapacity(minCapacity); 

 elementData = Arrays.copyOf(elementData, newCapacity); 

} 

public Vector(int initialCapacity) { 

 this(initialCapacity, 0); 

} 

public Vector() { 

 this(10); 

}也可以使⽤ concurrent 并发包下的 CopyOnWriteArrayList 类。 

**CopyOnWriteArrayList** 

\1. 读写分离 

写操作在⼀个复制的数组上进⾏，读操作还是在原始数组中进⾏，读写分离，互不影响。 

写操作需要加锁，防⽌并发写⼊时导致写⼊数据丢失。 

写操作结束之后需要把原始数组指向新的复制数组。 

\2. 适⽤场景 

List<String> list = new ArrayList<>(); 

List<String> synList = Collections.synchronizedList(list); 

List<String> list = new CopyOnWriteArrayList<>(); 

public boolean add(E e) { 

 final ReentrantLock lock = this.lock; 

 lock.lock(); 

 try { 

 Object[] elements = getArray(); 

 int len = elements.length; 

 Object[] newElements = Arrays.copyOf(elements, len + 1); 

 newElements[len] = e; 

 setArray(newElements); 

 return true; 

 } finally { 

 lock.unlock(); 

 } 

} 

final void setArray(Object[] a) { 

 array = a; 

} 

@SuppressWarnings("unchecked") 

private E get(Object[] a, int index) { 

 return (E) a[index]; 

}CopyOnWriteArrayList 在写操作的同时允许读操作，⼤⼤提⾼了读操作的性能，因此很适合读多写少的 

应⽤场景。 

但是 CopyOnWriteArrayList 有其缺陷： 

内存占⽤：在写操作时需要复制⼀个新的数组，使得内存占⽤为原来的两倍左右； 

数据不⼀致：读操作不能读取实时性的数据，因为部分写操作的数据还未同步到读数组中。 

所以 CopyOnWriteArrayList 不适合内存敏感以及对实时性要求很⾼的场景。 

**LinkedList** 

\1. 概览 

基于双向链表实现，使⽤ Node 存储链表节点信息。 

每个链表存储了 first 和 last 指针： 

\2. 与 ArrayList 的⽐较 

ArrayList 基于动态数组实现，LinkedList 基于双向链表实现。ArrayList 和 LinkedList 的区别可以归结 

为数组和链表的区别： 

数组⽀持随机访问，但插⼊删除的代价很⾼，需要移动⼤量元素； 

private static class Node<E> { 

 E item; 

 Node<E> next; 

 Node<E> prev; 

} 

transient Node<E> first; 

transient Node<E> last;链表不⽀持随机访问，但插⼊删除只需要改变指针。 

**HashMap** 

为了便于理解，以下源码分析以 JDK 1.7 为主。 

\1. 存储结构 

内部包含了⼀个 Entry 类型的数组 table。Entry 存储着键值对。它包含了四个字段，从 next 字段我们 

可以看出 Entry 是⼀个链表。即数组中的每个位置被当成⼀个桶，⼀个桶存放⼀个链表。HashMap 使 

⽤拉链法来解决冲突，同⼀个链表中存放哈希值和散列桶取模运算结果相同的 Entry。 

transient Entry[] table; 

static class Entry<K,V> implements Map.Entry<K,V> { 

 final K key; 

 V value; 

 Entry<K,V> next; 

 int hash; 

 Entry(int h, K k, V v, Entry<K,V> n) { 

 value = v; 

 next = n; 

 key = k; 

 hash = h;2. 拉链法的⼯作原理 

 } 

 public final K getKey() { 

 return key; 

 } 

 public final V getValue() { 

 return value; 

 } 

 public final V setValue(V newValue) { 

 V oldValue = value; 

 value = newValue; 

 return oldValue; 

 } 

 public final boolean equals(Object o) { 

 if (!(o instanceof Map.Entry)) 

 return false; 

 Map.Entry e = (Map.Entry)o; 

 Object k1 = getKey(); 

 Object k2 = e.getKey(); 

 if (k1 == k2 || (k1 != null && k1.equals(k2))) { 

 Object v1 = getValue(); 

 Object v2 = e.getValue(); 

 if (v1 == v2 || (v1 != null && v1.equals(v2))) 

 return true; 

 } 

 return false; 

 } 

 public final int hashCode() { 

 return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue()); 

 } 

 public final String toString() { 

 return getKey() + "=" + getValue(); 

 } 

}新建⼀个 HashMap，默认⼤⼩为 16； 

插⼊ <K1,V1> 键值对，先计算 K1 的 hashCode 为 115，使⽤除留余数法得到所在的桶下标 

115%16=3。 

插⼊ <K2,V2> 键值对，先计算 K2 的 hashCode 为 118，使⽤除留余数法得到所在的桶下标 

118%16=6。 

插⼊ <K3,V3> 键值对，先计算 K3 的 hashCode 为 118，使⽤除留余数法得到所在的桶下标 

118%16=6，插在 <K2,V2> 前⾯。 

应该注意到链表的插⼊是以头插法⽅式进⾏的，例如上⾯的 <K3,V3> 不是插在 <K2,V2> 后⾯，⽽是插 

⼊在链表头部。 

查找需要分成两步进⾏： 

计算键值对所在的桶； 

在链表上顺序查找，时间复杂度显然和链表的⻓度成正⽐。 

\3. put 操作 

HashMap<String, String> map = new HashMap<>(); 

map.put("K1", "V1"); 

map.put("K2", "V2"); 

map.put("K3", "V3"); 

public V put(K key, V value) { 

 if (table == EMPTY_TABLE) { inflateTable(threshold); 

 } 

 // 键为 null 单独处理 

 if (key == null) 

 return putForNullKey(value); 

 int hash = hash(key); 

 // 确定桶下标 

 int i = indexFor(hash, table.length); 

 // 先找出是否已经存在键为 key 的键值对，如果存在的话就更新这个键值对的值为 value 

 for (Entry<K,V> e = table[i]; e != null; e = e.next) { 

 Object k; 

 if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { 

 V oldValue = e.value; 

 e.value = value; 

 e.recordAccess(this); 

 return oldValue; 

 } 

 } 

 modCount++; 

 // 插⼊新键值对 

 addEntry(hash, key, value, i); 

 return null; 

} 

HashMap 允许插⼊键为 null 的键值对。但是因为⽆法调⽤ null 的 hashCode() ⽅法，也就⽆法确定该 

键值对的桶下标，只能通过强制指定⼀个桶下标来存放。HashMap 使⽤第 0 个桶存放键为 null 的键值 

对。 

private V putForNullKey(V value) { 

 for (Entry<K,V> e = table[0]; e != null; e = e.next) { 

 if (e.key == null) { 

 V oldValue = e.value; 

 e.value = value; 

 e.recordAccess(this); 

 return oldValue; 

 } 

 } 

 modCount++; 

 addEntry(0, null, value, 0); 

 return null; 

}使⽤链表的头插法，也就是新的键值对插在链表的头部，⽽不是链表的尾部。 

\4. 确定桶下标 

很多操作都需要先确定⼀个键值对所在的桶下标。 

**4.1** 计算 **hash** 值  

void addEntry(int hash, K key, V value, int bucketIndex) { 

 if ((size >= threshold) && (null != table[bucketIndex])) { 

 resize(2 * table.length); 

 hash = (null != key) ? hash(key) : 0; 

 bucketIndex = indexFor(hash, table.length); 

 } 

 createEntry(hash, key, value, bucketIndex); 

} 

void createEntry(int hash, K key, V value, int bucketIndex) { 

 Entry<K,V> e = table[bucketIndex]; 

 // 头插法，链表头部指向新的键值对 

 table[bucketIndex] = new Entry<>(hash, key, value, e); 

 size++; 

} 

Entry(int h, K k, V v, Entry<K,V> n) { 

 value = v; 

 next = n; 

 key = k; 

 hash = h; 

} 

int hash = hash(key); 

int i = indexFor(hash, table.length); 

final int hash(Object k) { 

 int h = hashSeed; 

 if (0 != h && k instanceof String) { 

 return sun.misc.Hashing.stringHash32((String) k); 

 } 

 h ^= k.hashCode(); // This function ensures that hashCodes that differ only by 

 // constant multiples at each bit position have a bounded 

 // number of collisions (approximately 8 at default load factor). 

 h ^= (h >>> 20) ^ (h >>> 12); 

 return h ^ (h >>> 7) ^ (h >>> 4); 

} 

public final int hashCode() { 

 return Objects.hashCode(key) ^ Objects.hashCode(value); 

} 

**4.2** 取模  

令 x = 1<<4，即 x 为 2 的 4 次⽅，它具有以下性质： 

x : 00010000 

x-1 : 00001111 

令⼀个数 y 与 x-1 做与运算，可以去除 y 位级表示的第 4 位以上数： 

y : 10110010 

x-1 : 00001111 

y&(x-1) : 00000010 

这个性质和 y 对 x 取模效果是⼀样的： 

y : 10110010 

x : 00010000 

y%x : 00000010 

我们知道，位运算的代价⽐求模运算⼩的多，因此在进⾏这种计算时⽤位运算的话能带来更⾼的性能。 

确定桶下标的最后⼀步是将 key 的 hash 值对桶个数取模：hash%capacity，如果能保证 capacity 为 2 

的 n 次⽅，那么就可以将这个操作转换为位运算。 

static int indexFor(int h, int length) { 

 return h & (length-1); 

} 

\5. 扩容-基本原理参数 

含义 

capacity 

table 的容量⼤⼩，默认为 16。需要注意的是 capacity 必须保证为 2 的 n 次⽅。 

size 

键值对数量。 

threshold 

size 的临界值，当 size ⼤于等于 threshold 就必须进⾏扩容操作。 

loadFactor 

装载因⼦，table 能够使⽤的⽐例，threshold = (int)(capacity* loadFactor)。 

\5. 扩容-基本原理 

设 HashMap 的 table ⻓度为 M，需要存储的键值对数量为 N，如果哈希函数满⾜均匀性的要求，那么 

每条链表的⻓度⼤约为 N/M，因此查找的复杂度为 O(N/M)。 

为了让查找的成本降低，应该使 N/M 尽可能⼩，因此需要保证 M 尽可能⼤，也就是说 table 要尽可能 

⼤。HashMap 采⽤动态扩容来根据当前的 N 值来调整 M 值，使得空间效率和时间效率都能得到保证。 

和扩容相关的参数主要有：capacity、size、threshold 和 load_factor。 

从下⾯的添加元素代码中可以看出，当需要扩容时，令 capacity 为原来的两倍。 

static final int DEFAULT_INITIAL_CAPACITY = 16; 

static final int MAXIMUM_CAPACITY = 1 << 30; 

static final float DEFAULT_LOAD_FACTOR = 0.75f; 

transient Entry[] table; 

transient int size; 

int threshold; 

final float loadFactor; 

transient int modCount; 

void addEntry(int hash, K key, V value, int bucketIndex) { 

 Entry<K,V> e = table[bucketIndex]; 

 table[bucketIndex] = new Entry<>(hash, key, value, e); 

 if (size++ >= threshold) 

 resize(2 * table.length); 

}扩容使⽤ resize() 实现，需要注意的是，扩容操作同样需要把 oldTable 的所有键值对重新插⼊ 

newTable 中，因此这⼀步是很费时的。 

\6. 扩容-重新计算桶下标 

在进⾏扩容时，需要把键值对重新计算桶下标，从⽽放到对应的桶上。在前⾯提到，HashMap 使⽤ 

hash%capacity 来确定桶下标。HashMap capacity 为 2 的 n 次⽅这⼀特点能够极⼤降低重新计算桶下 

标操作的复杂度。 

假设原数组⻓度 capacity 为 16，扩容之后 new capacity 为 32： 

void resize(int newCapacity) { 

 Entry[] oldTable = table; 

 int oldCapacity = oldTable.length; 

 if (oldCapacity == MAXIMUM_CAPACITY) { 

 threshold = Integer.MAX_VALUE; 

 return; 

 } 

 Entry[] newTable = new Entry[newCapacity]; 

 transfer(newTable); 

 table = newTable; 

 threshold = (int)(newCapacity * loadFactor); 

} 

void transfer(Entry[] newTable) { 

 Entry[] src = table; 

 int newCapacity = newTable.length; 

 for (int j = 0; j < src.length; j++) { 

 Entry<K,V> e = src[j]; 

 if (e != null) { 

 src[j] = null; 

 do { 

 Entry<K,V> next = e.next; 

 int i = indexFor(e.hash, newCapacity); 

 e.next = newTable[i]; 

 newTable[i] = e; 

 e = next; 

 } while (e != null); 

 } 

 } 

}对于⼀个 Key，它的哈希值 hash 在第 5 位： 

为 0，那么 hash%00010000 = hash%00100000，桶位置和原来⼀致； 

为 1，hash%00010000 = hash%00100000 + 16，桶位置是原位置 + 16。 

\7. 计算数组容量 

HashMap 构造函数允许⽤户传⼊的容量不是 2 的 n 次⽅，因为它可以⾃动地将传⼊的容量转换为 2 的 

n 次⽅。 

先考虑如何求⼀个数的掩码，对于 10010000，它的掩码为 11111111，可以使⽤以下⽅法得到： 

mask+1 是⼤于原始数字的最⼩的 2 的 n 次⽅。 

以下是 HashMap 中计算数组容量的代码： 

\8. 链表转红⿊树 

从 JDK 1.8 开始，⼀个桶存储的链表⻓度⼤于等于 8 时会将链表转换为红⿊树。 

\9. 与 Hashtable 的⽐较 

capacity : 00010000 

new capacity : 00100000 

mask |= mask >> 1 11011000 

mask |= mask >> 2 11111110 

mask |= mask >> 4 11111111 

num 10010000 

mask+1 100000000 

static final int tableSizeFor(int cap) { 

 int n = cap - 1; 

 n |= n >>> 1; 

 n |= n >>> 2; 

 n |= n >>> 4; 

 n |= n >>> 8; 

 n |= n >>> 16; 

 return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 

1;

}Hashtable 使⽤ synchronized 来进⾏同步。 

HashMap 可以插⼊键为 null 的 Entry。 

HashMap 的迭代器是 fail-fast 迭代器。 

HashMap 不能保证随着时间的推移 Map 中的元素次序是不变的。 

**ConcurrentHashMap** 

\1. 存储结构 

ConcurrentHashMap 和 HashMap 实现上类似，最主要的差别是 ConcurrentHashMap 采⽤了分段锁 

（Segment），每个分段锁维护着⼏个桶（HashEntry），多个线程可以同时访问不同分段锁上的桶， 

从⽽使其并发度更⾼（并发度就是 Segment 的个数）。 

Segment 继承⾃ ReentrantLock。 

static final class HashEntry<K,V> { 

 final int hash; 

 final K key; 

 volatile V value; 

 volatile HashEntry<K,V> next; 

}默认的并发级别为 16，也就是说默认创建 16 个 Segment。 

\2. size 操作 

每个 Segment 维护了⼀个 count 变量来统计该 Segment 中的键值对个数。 

在执⾏ size 操作时，需要遍历所有 Segment 然后把 count 累计起来。 

ConcurrentHashMap 在执⾏ size 操作时先尝试不加锁，如果连续两次不加锁操作得到的结果⼀致，那 

么可以认为这个结果是正确的。 

尝试次数使⽤ RETRIES_BEFORE_LOCK 定义，该值为 2，retries 初始值为 -1，因此尝试次数为 3。 

static final class Segment<K,V> extends ReentrantLock implements 

Serializable { 

 private static final long serialVersionUID = 2249069246763182397L; 

 static final int MAX_SCAN_RETRIES = 

 Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1; 

 transient volatile HashEntry<K,V>[] table; 

 transient int count; 

 transient int modCount; 

 transient int threshold; 

 final float loadFactor; 

} 

final Segment<K,V>[] segments; 

static final int DEFAULT_CONCURRENCY_LEVEL = 16; 

/** 

\* The number of elements. Accessed only either within locks 

\* or among other volatile reads that maintain visibility. 

*/ 

transient int count;如果尝试的次数超过 3 次，就需要对每个 Segment 加锁。 

/** 

\* Number of unsynchronized retries in size and containsValue 

\* methods before resorting to locking. This is used to avoid 

\* unbounded retries if tables undergo continuous modification 

\* which would make it impossible to obtain an accurate result. 

*/ 

static final int RETRIES_BEFORE_LOCK = 2; 

public int size() { 

 // Try a few times to get accurate count. On failure due to 

 // continuous async changes in table, resort to locking. 

 final Segment<K,V>[] segments = this.segments; 

 int size; 

 boolean overflow; // true if size overflows 32 bits 

 long sum; // sum of modCounts 

 long last = 0L; // previous sum 

 int retries = -1; // first iteration isn't retry 

 try { 

 for (;;) { 

 // 超过尝试次数，则对每个 Segment 加锁 

 if (retries++ == RETRIES_BEFORE_LOCK) { 

 for (int j = 0; j < segments.length; ++j) 

 ensureSegment(j).lock(); // force creation 

 } 

 sum = 0L; 

 size = 0; 

 overflow = false; 

 for (int j = 0; j < segments.length; ++j) { 

 Segment<K,V> seg = segmentAt(segments, j); 

 if (seg != null) { 

 sum += seg.modCount; 

 int c = seg.count; 

 if (c < 0 || (size += c) < 0) 

 overflow = true; 

 } 

 } 

 // 连续两次得到的结果⼀致，则认为这个结果是正确的 

 if (sum == last) 

 break; 

 last = sum; 

 }3. JDK 1.8 的改动 

JDK 1.7 使⽤分段锁机制来实现并发更新操作，核⼼类为 Segment，它继承⾃重⼊锁 ReentrantLock， 

并发度与 Segment 数量相等。 

JDK 1.8 使⽤了 CAS 操作来⽀持更⾼的并发度，在 CAS 操作失败时使⽤内置锁 synchronized。 

并且 JDK 1.8 的实现也在链表过⻓时会转换为红⿊树。 

**LinkedHashMap** 

存储结构 

继承⾃ HashMap，因此具有和 HashMap ⼀样的快速查找特性。 

内部维护了⼀个双向链表，⽤来维护插⼊顺序或者 LRU 顺序。 

accessOrder 决定了顺序，默认为 false，此时维护的是插⼊顺序。 

 } finally { 

 if (retries > RETRIES_BEFORE_LOCK) { 

 for (int j = 0; j < segments.length; ++j) 

 segmentAt(segments, j).unlock(); 

 } 

 } 

 return overflow ? Integer.MAX_VALUE : size; 

} 

public class LinkedHashMap<K,V> extends HashMap<K,V> implements Map<K,V> 

/** 

\* The head (eldest) of the doubly linked list. 

*/ 

transient LinkedHashMap.Entry<K,V> head; 

/** 

\* The tail (youngest) of the doubly linked list. 

*/ 

transient LinkedHashMap.Entry<K,V> tail; 

final boolean accessOrder;LinkedHashMap 最重要的是以下⽤于维护顺序的函数，它们会在 put、get 等⽅法中调⽤。 

afterNodeAccess() 

当⼀个节点被访问时，如果 accessOrder 为 true，则会将该节点移到链表尾部。也就是说指定为 LRU 

顺序之后，在每次访问⼀个节点时，会将这个节点移到链表尾部，保证链表尾部是最近访问的节点，那 

么链表⾸部就是最近最久未使⽤的节点。 

afterNodeInsertion() 

在 put 等操作之后执⾏，当 removeEldestEntry() ⽅法返回 true 时会移除最晚的节点，也就是链表⾸部 

节点 first。 

evict 只有在构建 Map 的时候才为 false，在这⾥为 true。 

void afterNodeAccess(Node<K,V> p) { } 

void afterNodeInsertion(boolean evict) { } 

void afterNodeAccess(Node<K,V> e) { // move node to last 

 LinkedHashMap.Entry<K,V> last; 

 if (accessOrder && (last = tail) != e) { 

 LinkedHashMap.Entry<K,V> p = 

 (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after; 

 p.after = null; 

 if (b == null) 

 head = a; 

 else 

 b.after = a; 

 if (a != null) 

 a.before = b; 

 else 

 last = b; 

 if (last == null) 

 head = p; 

 else { 

 p.before = last; 

 last.after = p; 

 } 

 tail = p; 

 ++modCount; 

 } 

}removeEldestEntry() 默认为 false，如果需要让它为 true，需要继承 LinkedHashMap 并且覆盖这个⽅ 

法的实现，这在实现 LRU 的缓存中特别有⽤，通过移除最近最久未使⽤的节点，从⽽保证缓存空间⾜ 

够，并且缓存的数据都是热点数据。 

LRU 缓存 

以下是使⽤ LinkedHashMap 实现的⼀个 LRU 缓存： 

设定最⼤缓存空间 MAX_ENTRIES 为 3； 

使⽤ LinkedHashMap 的构造函数将 accessOrder 设置为 true，开启 LRU 顺序； 

覆盖 removeEldestEntry() ⽅法实现，在节点多于 MAX_ENTRIES 就会将最近最久未使⽤的数据移 

除。 

void afterNodeInsertion(boolean evict) { // possibly remove eldest 

 LinkedHashMap.Entry<K,V> first; 

 if (evict && (first = head) != null && removeEldestEntry(first)) { 

 K key = first.key; 

 removeNode(hash(key), key, null, false, true); 

 } 

} 

protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { 

 return false; 

} 

class LRUCache<K, V> extends LinkedHashMap<K, V> { 

 private static final int MAX_ENTRIES = 3; 

 protected boolean removeEldestEntry(Map.Entry eldest) { 

 return size() > MAX_ENTRIES; 

 } 

 LRUCache() { 

 super(MAX_ENTRIES, 0.75f, true); 

 } 

}**WeakHashMap** 

存储结构 

WeakHashMap 的 Entry 继承⾃ WeakReference，被 WeakReference 关联的对象在下⼀次垃圾回收时 

会被回收。 

WeakHashMap 主要⽤来实现缓存，通过使⽤ WeakHashMap 来引⽤缓存对象，由 JVM 对这部分缓存 

进⾏回收。 

ConcurrentCache 

Tomcat 中的 ConcurrentCache 使⽤了 WeakHashMap 来实现缓存功能。 

ConcurrentCache 采取的是分代缓存： 

经常使⽤的对象放⼊ eden 中，eden 使⽤ ConcurrentHashMap 实现，不⽤担⼼会被回收（伊甸 

园）； 

不常⽤的对象放⼊ longterm，longterm 使⽤ WeakHashMap 实现，这些⽼对象会被垃圾收集器回 

收。 

当调⽤ get() ⽅法时，会先从 eden 区获取，如果没有找到的话再到 longterm 获取，当从 longterm 

获取到就把对象放⼊ eden 中，从⽽保证经常被访问的节点不容易被回收。 

当调⽤ put() ⽅法时，如果 eden 的⼤⼩超过了 size，那么就将 eden 中的所有对象都放⼊ longterm 

中，利⽤虚拟机回收掉⼀部分不经常使⽤的对象。 

public static void main(String[] args) { 

 LRUCache<Integer, String> cache = new LRUCache<>(); 

 cache.put(1, "a"); 

 cache.put(2, "b"); 

 cache.put(3, "c"); 

 cache.get(1); 

 cache.put(4, "d"); 

 System.out.println(cache.keySet()); 

} 

[3, 1, 4] 

private static class Entry<K,V> extends WeakReference<Object> implements 

Map.Entry<K,V> 

public final class ConcurrentCache<K, V> {参考资料 

Eckel B. Java 编程思想 [M]. 机械⼯业出版社, 2002. 

Java Collection Framework 

Iterator 模式 

Java 8 系列之重新认识 HashMap 

What is difference between HashMap and Hashtable in Java? 

Java 集合之 HashMap 

The principle of ConcurrentHashMap analysis 

探索 ConcurrentHashMap ⾼并发性的实现机制 

HashMap 相关⾯试题及其解答 

Java 集合细节（⼆）：asList 的缺陷 

Java Collection Framework – The LinkedList Class 

 private final int size; 

 private final Map<K, V> eden; 

 private final Map<K, V> longterm; 

 public ConcurrentCache(int size) { 

 this.size = size; 

 this.eden = new ConcurrentHashMap<>(size); 

 this.longterm = new WeakHashMap<>(size); 

 } 

 public V get(K k) { 

 V v = this.eden.get(k); 

 if (v == null) { 

 v = this.longterm.get(k); 

 if (v != null) 

 this.eden.put(k, v); 

 } 

 return v; 

 } 

 public void put(K k, V v) { 

 if (this.eden.size() >= size) { 

 this.longterm.putAll(this.eden); 

 this.eden.clear(); 

 } 

 this.eden.put(k, v); 

 } 

}Java 并发 

Java 并发 

⼀、使⽤线程 

实现 Runnable 接⼝ 

实现 Callable 接⼝ 

继承 Thread 类 

实现接⼝ VS 继承 Thread 

⼆、基础线程机制 

Executor 

Daemon 

sleep() 

yield() 

三、中断 

InterruptedException 

interrupted() 

Executor 的中断操作 

四、互斥同步 

synchronized 

ReentrantLock 

⽐较 

使⽤选择 

五、线程之间的协作 

join() 

wait() notify() notifyAll() 

await() signal() signalAll() 

六、线程状态 

新建（NEW） 

可运⾏（RUNABLE） 

阻塞（BLOCKED） 

⽆限期等待（WAITING） 

限期等待（TIMED_WAITING） 

死亡（TERMINATED） 

七、J.U.C - AQS 

CountDownLatch 

CyclicBarrier 

Semaphore 

⼋、J.U.C - 其它组件 

FutureTaskBlockingQueue 

ForkJoin 

九、线程不安全示例 

⼗、Java 内存模型 

主内存与⼯作内存 

内存间交互操作 

内存模型三⼤特性 

先⾏发⽣原则 

⼗⼀、线程安全 

不可变 

互斥同步 

⾮阻塞同步 

⽆同步⽅案 

⼗⼆、锁优化 

⾃旋锁 

锁消除 

锁粗化 

轻量级锁 

偏向锁 

⼗三、多线程开发良好的实践 

参考资料 

⼀、使⽤线程 

有三种使⽤线程的⽅法： 

实现 Runnable 接⼝； 

实现 Callable 接⼝； 

继承 Thread 类。 

实现 Runnable 和 Callable 接⼝的类只能当做⼀个可以在线程中运⾏的任务，不是真正意义上的线程， 

因此最后还需要通过 Thread 来调⽤。可以理解为任务是通过线程驱动从⽽执⾏的。 

实现 **Runnable** 接⼝ 

需要实现接⼝中的 run() ⽅法。使⽤ Runnable 实例再创建⼀个 Thread 实例，然后调⽤ Thread 实例的 start() ⽅法来启动线程。 

实现 **Callable** 接⼝ 

与 Runnable 相⽐，Callable 可以有返回值，返回值通过 FutureTask 进⾏封装。 

继承 **Thread** 类 

同样也是需要实现 run() ⽅法，因为 Thread 类也实现了 Runable 接⼝。 

当调⽤ start() ⽅法启动⼀个线程时，虚拟机会将该线程放⼊就绪队列中等待被调度，当⼀个线程被调度 

时会执⾏该线程的 run() ⽅法。 

public class MyRunnable implements Runnable { 

 @Override 

 public void run() { 

 // ... 

 } 

} 

public static void main(String[] args) { 

 MyRunnable instance = new MyRunnable(); 

 Thread thread = new Thread(instance); 

 thread.start(); 

} 

public class MyCallable implements Callable<Integer> { 

 public Integer call() { 

 return 123; 

 } 

} 

public static void main(String[] args) throws ExecutionException, 

InterruptedException { 

 MyCallable mc = new MyCallable(); 

 FutureTask<Integer> ft = new FutureTask<>(mc); 

 Thread thread = new Thread(ft); 

 thread.start(); 

 System.out.println(ft.get()); 

}实现接⼝ **VS** 继承 **Thread** 

实现接⼝会更好⼀些，因为： 

Java 不⽀持多重继承，因此继承了 Thread 类就⽆法继承其它类，但是可以实现多个接⼝； 

类可能只要求可执⾏就⾏，继承整个 Thread 类开销过⼤。 

⼆、基础线程机制 

**Executor** 

Executor 管理多个异步任务的执⾏，⽽⽆需程序员显式地管理线程的⽣命周期。这⾥的异步是指多个任 

务的执⾏互不⼲扰，不需要进⾏同步操作。 

主要有三种 Executor： 

CachedThreadPool：⼀个任务创建⼀个线程； 

FixedThreadPool：所有任务只能使⽤固定⼤⼩的线程； 

SingleThreadExecutor：相当于⼤⼩为 1 的 FixedThreadPool。 

**Daemon** 

守护线程是程序运⾏时在后台提供服务的线程，不属于程序中不可或缺的部分。 

public class MyThread extends Thread { 

 public void run() { 

 // ... 

 } 

} 

public static void main(String[] args) { 

 MyThread mt = new MyThread(); 

 mt.start(); 

} 

public static void main(String[] args) { 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < 5; i++) { 

 executorService.execute(new MyRunnable()); 

 } 

 executorService.shutdown(); 

}当所有⾮守护线程结束时，程序也就终⽌，同时会杀死所有守护线程。 

main() 属于⾮守护线程。 

在线程启动之前使⽤ setDaemon() ⽅法可以将⼀个线程设置为守护线程。 

**sleep()** 

Thread.sleep(millisec) ⽅法会休眠当前正在执⾏的线程，millisec 单位为毫秒。 

sleep() 可能会抛出 InterruptedException，因为异常不能跨线程传播回 main() 中，因此必须在本地进⾏ 

处理。线程中抛出的其它异常也同样需要在本地进⾏处理。 

**yield()** 

对静态⽅法 Thread.yield() 的调⽤声明了当前线程已经完成了⽣命周期中最重要的部分，可以切换给其 

它线程来执⾏。该⽅法只是对线程调度器的⼀个建议，⽽且也只是建议具有相同优先级的其它线程可以 

运⾏。 

三、中断 

⼀个线程执⾏完毕之后会⾃动结束，如果在运⾏过程中发⽣异常也会提前结束。 

**InterruptedException** 

public static void main(String[] args) { 

 Thread thread = new Thread(new MyRunnable()); 

 thread.setDaemon(true); 

} 

public void run() { 

 try { 

 Thread.sleep(3000); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

} 

public void run() { 

 Thread.yield(); 

}通过调⽤⼀个线程的 interrupt() 来中断该线程，如果该线程处于阻塞、限期等待或者⽆限期等待状态， 

那么就会抛出 InterruptedException，从⽽提前结束该线程。但是不能中断 I/O 阻塞和 synchronized 锁 

阻塞。 

对于以下代码，在 main() 中启动⼀个线程之后再中断它，由于线程中调⽤了 Thread.sleep() ⽅法，因此 

会抛出⼀个 InterruptedException，从⽽提前结束线程，不执⾏之后的语句。 

**interrupted()** 

如果⼀个线程的 run() ⽅法执⾏⼀个⽆限循环，并且没有执⾏ sleep() 等会抛出 InterruptedException 的 

操作，那么调⽤线程的 interrupt() ⽅法就⽆法使线程提前结束。 

public class InterruptExample { 

 private static class MyThread1 extends Thread { 

 @Override 

 public void run() { 

 try { 

 Thread.sleep(2000); 

 System.out.println("Thread run"); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 } 

 } 

} 

public static void main(String[] args) throws InterruptedException { 

 Thread thread1 = new MyThread1(); 

 thread1.start(); 

 thread1.interrupt(); 

 System.out.println("Main run"); 

} 

Main run 

java.lang.InterruptedException: sleep interrupted 

 at java.lang.Thread.sleep(Native Method) 

 at InterruptExample.lambda$main$0(InterruptExample.java:5) 

 at InterruptExample$$Lambda$1/713338599.run(Unknown Source) 

 at java.lang.Thread.run(Thread.java:745)但是调⽤ interrupt() ⽅法会设置线程的中断标记，此时调⽤ interrupted() ⽅法会返回 true。因此可以在 

循环体中使⽤ interrupted() ⽅法来判断线程是否处于中断状态，从⽽提前结束线程。 

**Executor** 的中断操作 

调⽤ Executor 的 shutdown() ⽅法会等待线程都执⾏完毕之后再关闭，但是如果调⽤的是 

shutdownNow() ⽅法，则相当于调⽤每个线程的 interrupt() ⽅法。 

以下使⽤ Lambda 创建线程，相当于创建了⼀个匿名内部线程。 

public class InterruptExample { 

 private static class MyThread2 extends Thread { 

 @Override 

 public void run() { 

 while (!interrupted()) { 

 // .. 

 } 

 System.out.println("Thread end"); 

 } 

 } 

} 

public static void main(String[] args) throws InterruptedException { 

 Thread thread2 = new MyThread2(); 

 thread2.start(); 

 thread2.interrupt(); 

} 

Thread end如果只想中断 Executor 中的⼀个线程，可以通过使⽤ submit() ⽅法来提交⼀个线程，它会返回⼀个 

Future<?> 对象，通过调⽤该对象的 cancel(true) ⽅法就可以中断线程。 

四、互斥同步 

Java 提供了两种锁机制来控制多个线程对共享资源的互斥访问，第⼀个是 JVM 实现的 synchronized， 

⽽另⼀个是 JDK 实现的 ReentrantLock。 

**synchronized** 

public static void main(String[] args) { 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> { 

 try { 

 Thread.sleep(2000); 

 System.out.println("Thread run"); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 }); 

 executorService.shutdownNow(); 

 System.out.println("Main run"); 

} 

Main run 

java.lang.InterruptedException: sleep interrupted 

 at java.lang.Thread.sleep(Native Method) 

 at 

ExecutorInterruptExample.lambda$main$0(ExecutorInterruptExample.java:9) 

 at ExecutorInterruptExample$$Lambda$1/1160460865.run(Unknown Source) 

 at 

java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1 

142) 

 at 

java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java: 

617) 

 at java.lang.Thread.run(Thread.java:745) 

Future<?> future = executorService.submit(() -> { 

 // .. 

}); 

future.cancel(true);**1.** 同步⼀个代码块  

public void func() { 

 synchronized (this) { 

 // ... 

 } 

} 

它只作⽤于同⼀个对象，如果调⽤两个对象上的同步代码块，就不会进⾏同步。 

对于以下代码，使⽤ ExecutorService 执⾏了两个线程，由于调⽤的是同⼀个对象的同步代码块，因此 

这两个线程会进⾏同步，当⼀个线程进⼊同步语句块时，另⼀个线程就必须等待。 

public class SynchronizedExample { 

 public void func1() { 

 synchronized (this) { 

 for (int i = 0; i < 10; i++) { 

 System.out.print(i + " "); 

 } 

 } 

 } 

} 

public static void main(String[] args) { 

 SynchronizedExample e1 = new SynchronizedExample(); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> e1.func1()); 

 executorService.execute(() -> e1.func1()); 

} 

0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 

对于以下代码，两个线程调⽤了不同对象的同步代码块，因此这两个线程就不需要同步。从输出结果可 

以看出，两个线程交叉执⾏。public static void main(String[] args) { 

 SynchronizedExample e1 = new SynchronizedExample(); 

 SynchronizedExample e2 = new SynchronizedExample(); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> e1.func1()); 

 executorService.execute(() -> e2.func1()); 

} 

0 0 1 1 2 2 3 3 4 4 5 5 6 6 7 7 8 8 9 9 

**2.** 同步⼀个⽅法  

public synchronized void func () { 

 // ... 

} 

它和同步代码块⼀样，作⽤于同⼀个对象。 

**3.** 同步⼀个类  

public void func() { 

 synchronized (SynchronizedExample.class) { 

 // ... 

 } 

} 

作⽤于整个类，也就是说两个线程调⽤同⼀个类的不同对象上的这种同步语句，也会进⾏同步。 

public class SynchronizedExample { 

 public void func2() { 

 synchronized (SynchronizedExample.class) { 

 for (int i = 0; i < 10; i++) { 

 System.out.print(i + " "); 

 } 

 } 

 } 

}**4.** 同步⼀个静态⽅法  

作⽤于整个类。 

**ReentrantLock** 

ReentrantLock 是 java.util.concurrent（J.U.C）包中的锁。 

public static void main(String[] args) { 

 SynchronizedExample e1 = new SynchronizedExample(); 

 SynchronizedExample e2 = new SynchronizedExample(); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> e1.func2()); 

 executorService.execute(() -> e2.func2()); 

} 

0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 

public synchronized static void fun() { 

 // ... 

} 

public class LockExample { 

 private Lock lock = new ReentrantLock(); 

 public void func() { 

 lock.lock(); 

 try { 

 for (int i = 0; i < 10; i++) { 

 System.out.print(i + " "); 

 } 

 } finally { 

 lock.unlock(); // 确保释放锁，从⽽避免发⽣死锁。 

 } 

 } 

}⽐较 

**1.** 锁的实现  

synchronized 是 JVM 实现的，⽽ ReentrantLock 是 JDK 实现的。 

**2.** 性能  

新版本 Java 对 synchronized 进⾏了很多优化，例如⾃旋锁等，synchronized 与 ReentrantLock ⼤致 

相同。 

**3.** 等待可中断  

当持有锁的线程⻓期不释放锁的时候，正在等待的线程可以选择放弃等待，改为处理其他事情。 

ReentrantLock 可中断，⽽ synchronized 不⾏。 

**4.** 公平锁  

公平锁是指多个线程在等待同⼀个锁时，必须按照申请锁的时间顺序来依次获得锁。 

synchronized 中的锁是⾮公平的，ReentrantLock 默认情况下也是⾮公平的，但是也可以是公平的。 

**5.** 锁绑定多个条件  

⼀个 ReentrantLock 可以同时绑定多个 Condition 对象。 

使⽤选择 

除⾮需要使⽤ ReentrantLock 的⾼级功能，否则优先使⽤ synchronized。这是因为 synchronized 是 

JVM 实现的⼀种锁机制，JVM 原⽣地⽀持它，⽽ ReentrantLock 不是所有的 JDK 版本都⽀持。并且使 

⽤ synchronized 不⽤担⼼没有释放锁⽽导致死锁问题，因为 JVM 会确保锁的释放。 

五、线程之间的协作 

public static void main(String[] args) { 

 LockExample lockExample = new LockExample(); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> lockExample.func()); 

 executorService.execute(() -> lockExample.func()); 

} 

0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9当多个线程可以⼀起⼯作去解决某个问题时，如果某些部分必须在其它部分之前完成，那么就需要对线 

程进⾏协调。 

**join()** 

在线程中调⽤另⼀个线程的 join() ⽅法，会将当前线程挂起，⽽不是忙等待，直到⽬标线程结束。 

对于以下代码，虽然 b 线程先启动，但是因为在 b 线程中调⽤了 a 线程的 join() ⽅法，b 线程会等待 a 

线程结束才继续执⾏，因此最后能够保证 a 线程的输出先于 b 线程的输出。 

public class JoinExample { 

 private class A extends Thread { 

 @Override 

 public void run() { 

 System.out.println("A"); 

 } 

 } 

 private class B extends Thread { 

 private A a; 

 B(A a) { 

 this.a = a; 

 } 

 @Override 

 public void run() { 

 try { 

 a.join(); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 System.out.println("B"); 

 } 

 } 

 public void test() { 

 A a = new A(); 

 B b = new B(a); 

 b.start(); 

 a.start(); 

 }**wait() notify() notifyAll()** 

调⽤ wait() 使得线程等待某个条件满⾜，线程在等待时会被挂起，当其他线程的运⾏使得这个条件满⾜ 

时，其它线程会调⽤ notify() 或者 notifyAll() 来唤醒挂起的线程。 

它们都属于 Object 的⼀部分，⽽不属于 Thread。 

只能⽤在同步⽅法或者同步控制块中使⽤，否则会在运⾏时抛出 IllegalMonitorStateException。 

使⽤ wait() 挂起期间，线程会释放锁。这是因为，如果没有释放锁，那么其它线程就⽆法进⼊对象的同 

步⽅法或者同步控制块中，那么就⽆法执⾏ notify() 或者 notifyAll() 来唤醒挂起的线程，造成死锁。 

} 

public static void main(String[] args) { 

 JoinExample example = new JoinExample(); 

 example.test(); 

}

A

B 

public class WaitNotifyExample { 

 public synchronized void before() { 

 System.out.println("before"); 

 notifyAll(); 

 } 

 public synchronized void after() { 

 try { 

 wait(); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 System.out.println("after"); 

 } 

}**wait()** 和 **sleep()** 的区别  

wait() 是 Object 的⽅法，⽽ sleep() 是 Thread 的静态⽅法； 

wait() 会释放锁，sleep() 不会。 

**await() signal() signalAll()** 

java.util.concurrent 类库中提供了 Condition 类来实现线程之间的协调，可以在 Condition 上调⽤ 

await() ⽅法使线程等待，其它线程调⽤ signal() 或 signalAll() ⽅法唤醒等待的线程。 

相⽐于 wait() 这种等待⽅式，await() 可以指定等待的条件，因此更加灵活。 

使⽤ Lock 来获取⼀个 Condition 对象。 

public static void main(String[] args) { 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 WaitNotifyExample example = new WaitNotifyExample(); 

 executorService.execute(() -> example.after()); 

 executorService.execute(() -> example.before()); 

} 

before 

after 

public class AwaitSignalExample { 

 private Lock lock = new ReentrantLock(); 

 private Condition condition = lock.newCondition(); 

 public void before() { 

 lock.lock(); 

 try { 

 System.out.println("before"); 

 condition.signalAll(); 

 } finally { 

 lock.unlock(); 

 } 

 } 

 public void after() { 

 lock.lock(); 

 try { 

 condition.await();六、线程状态 

⼀个线程只能处于⼀种状态，并且这⾥的线程状态特指 Java 虚拟机的线程状态，不能反映线程在特定 

操作系统下的状态。 

新建（**NEW**） 

创建后尚未启动。 

可运⾏（**RUNABLE**） 

正在 Java 虚拟机中运⾏。但是在操作系统层⾯，它可能处于运⾏状态，也可能等待资源调度（例如处 

理器资源），资源调度完成就进⼊运⾏状态。所以该状态的可运⾏是指可以被运⾏，具体有没有运⾏要 

看底层操作系统的资源调度。 

阻塞（**BLOCKED**） 

请求获取 monitor lock 从⽽进⼊ synchronized 函数或者代码块，但是其它线程已经占⽤了该 monitor 

lock，所以出于阻塞状态。要结束该状态进⼊从⽽ RUNABLE 需要其他线程释放 monitor lock。 

⽆限期等待（**WAITING**） 

等待其它线程显式地唤醒。 

 System.out.println("after"); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } finally { 

 lock.unlock(); 

 } 

 } 

} 

public static void main(String[] args) { 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 AwaitSignalExample example = new AwaitSignalExample(); 

 executorService.execute(() -> example.after()); 

 executorService.execute(() -> example.before()); 

} 

before 

after进⼊⽅法 

退出⽅法 

没有设置 Timeout 参数的 Object.wait() ⽅法 

Object.notify() / Object.notifyAll() 

没有设置 Timeout 参数的 Thread.join() ⽅法 

被调⽤的线程执⾏完毕 

LockSupport.park() ⽅法 

LockSupport.unpark(Thread) 

进⼊⽅法 

退出⽅法 

Thread.sleep() ⽅法 

时间结束 

设置了 Timeout 参数的 Object.wait() ⽅法 

时间结束 / Object.notify() / Object.notifyAll() 

设置了 Timeout 参数的 Thread.join() ⽅法 

时间结束 / 被调⽤的线程执⾏完毕 

LockSupport.parkNanos() ⽅法 

LockSupport.unpark(Thread) 

LockSupport.parkUntil() ⽅法 

LockSupport.unpark(Thread) 

阻塞和等待的区别在于，阻塞是被动的，它是在等待获取 monitor lock。⽽等待是主动的，通过调⽤  

Object.wait() 等⽅法进⼊。 

限期等待（**TIMED_WAITING**） 

⽆需等待其它线程显式地唤醒，在⼀定时间之后会被系统⾃动唤醒。 

调⽤ Thread.sleep() ⽅法使线程进⼊限期等待状态时，常常⽤“使⼀个线程睡眠”进⾏描述。调⽤ 

Object.wait() ⽅法使线程进⼊限期等待或者⽆限期等待时，常常⽤“挂起⼀个线程”进⾏描述。睡眠和挂 

起是⽤来描述⾏为，⽽阻塞和等待⽤来描述状态。 

死亡（**TERMINATED**） 

可以是线程结束任务之后⾃⼰结束，或者产⽣了异常⽽结束。 

Java SE 9 Enum Thread.State 

七、**J.U.C - AQS** 

java.util.concurrent（J.U.C）⼤⼤提⾼了并发性能，AQS 被认为是 J.U.C 的核⼼。 

**CountDownLatch** 

⽤来控制⼀个或者多个线程等待多个线程。 

维护了⼀个计数器 cnt，每次调⽤ countDown() ⽅法会让计数器的值减 1，减到 0 的时候，那些因为调 

⽤ await() ⽅法⽽在等待的线程就会被唤醒。**CyclicBarrier** 

⽤来控制多个线程互相等待，只有当多个线程都到达时，这些线程才会继续执⾏。 

和 CountdownLatch 相似，都是通过维护计数器来实现的。线程执⾏ await() ⽅法之后计数器会减 1， 

并进⾏等待，直到计数器为 0，所有调⽤ await() ⽅法⽽在等待的线程才能继续执⾏。 

public class CountdownLatchExample { 

 public static void main(String[] args) throws InterruptedException { 

 final int totalThread = 10; 

 CountDownLatch countDownLatch = new CountDownLatch(totalThread); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < totalThread; i++) { 

 executorService.execute(() -> { 

 System.out.print("run.."); 

 countDownLatch.countDown(); 

 }); 

 } 

 countDownLatch.await(); 

 System.out.println("end"); 

 executorService.shutdown(); 

 } 

} 

run..run..run..run..run..run..run..run..run..run..endCyclicBarrier 和 CountdownLatch 的⼀个区别是，CyclicBarrier 的计数器通过调⽤ reset() ⽅法可以循 

环使⽤，所以它才叫做循环屏障。 

CyclicBarrier 有两个构造函数，其中 parties 指示计数器的初始值，barrierAction 在所有线程都到达屏 

障的时候会执⾏⼀次。 

public CyclicBarrier(int parties, Runnable barrierAction) { 

 if (parties <= 0) throw new IllegalArgumentException(); 

 this.parties = parties; 

 this.count = parties; 

 this.barrierCommand = barrierAction; 

} 

public CyclicBarrier(int parties) { 

 this(parties, null); 

} 

public class CyclicBarrierExample { 

 public static void main(String[] args) { 

 final int totalThread = 10; 

 CyclicBarrier cyclicBarrier = new CyclicBarrier(totalThread); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < totalThread; i++) { 

 executorService.execute(() -> { 

 System.out.print("before.."); 

 try { 

 cyclicBarrier.await(); 

 } catch (InterruptedException | BrokenBarrierException e) { 

 e.printStackTrace(); 

 }**Semaphore** 

Semaphore 类似于操作系统中的信号量，可以控制对互斥资源的访问线程数。 

以下代码模拟了对某个服务的并发请求，每次只能有 3 个客户端同时访问，请求总数为 10。 

⼋、**J.U.C -** 其它组件 

 System.out.print("after.."); 

 }); 

 } 

 executorService.shutdown(); 

 } 

} 

before..before..before..before..before..before..before..before..before..bef 

ore..after..after..after..after..after..after..after..after..after..after.. 

public class SemaphoreExample { 

 public static void main(String[] args) { 

 final int clientCount = 3; 

 final int totalRequestCount = 10; 

 Semaphore semaphore = new Semaphore(clientCount); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < totalRequestCount; i++) { 

 executorService.execute(()->{ 

 try { 

 semaphore.acquire(); 

 System.out.print(semaphore.availablePermits() + " "); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } finally { 

 semaphore.release(); 

 } 

 }); 

 } 

 executorService.shutdown(); 

 } 

} 

2 1 2 2 2 2 2 1 2 2**FutureTask** 

在介绍 Callable 时我们知道它可以有返回值，返回值通过 Future<V> 进⾏封装。FutureTask 实现了 

RunnableFuture 接⼝，该接⼝继承⾃ Runnable 和 Future<V> 接⼝，这使得 FutureTask 既可以当做⼀ 

个任务执⾏，也可以有返回值。 

FutureTask 可⽤于异步获取执⾏结果或取消执⾏任务的场景。当⼀个计算任务需要执⾏很⻓时间，那么 

就可以⽤ FutureTask 来封装这个任务，主线程在完成⾃⼰的任务之后再去获取结果。 

public class FutureTask<V> implements RunnableFuture<V> 

public interface RunnableFuture<V> extends Runnable, Future<V> 

public class FutureTaskExample { 

 public static void main(String[] args) throws ExecutionException, 

InterruptedException { 

 FutureTask<Integer> futureTask = new FutureTask<Integer>(new 

Callable<Integer>() { 

 @Override 

 public Integer call() throws Exception { 

 int result = 0; 

 for (int i = 0; i < 100; i++) { 

 Thread.sleep(10); 

 result += i; 

 } 

 return result; 

 } 

 }); 

 Thread computeThread = new Thread(futureTask); 

 computeThread.start(); 

 Thread otherThread = new Thread(() -> { 

 System.out.println("other task is running..."); 

 try { 

 Thread.sleep(1000); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 }); 

 otherThread.start(); 

 System.out.println(futureTask.get());**BlockingQueue** 

java.util.concurrent.BlockingQueue 接⼝有以下阻塞队列的实现： 

**FIFO** 队列 ：LinkedBlockingQueue、ArrayBlockingQueue（固定⻓度） 

优先级队列 ：PriorityBlockingQueue 

提供了阻塞的 take() 和 put() ⽅法：如果队列为空 take() 将阻塞，直到队列中有内容；如果队列为满 

put() 将阻塞，直到队列有空闲位置。 

使⽤ **BlockingQueue** 实现⽣产者消费者问题  

 } 

} 

other task is running... 

4950 

public class ProducerConsumer { 

 private static BlockingQueue<String> queue = new ArrayBlockingQueue<> 

(5); 

 private static class Producer extends Thread { 

 @Override 

 public void run() { 

 try { 

 queue.put("product"); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 System.out.print("produce.."); 

 } 

 } 

 private static class Consumer extends Thread { 

 @Override 

 public void run() { 

 try { 

 String product = queue.take(); 

 } catch (InterruptedException e) { 

 e.printStackTrace();**ForkJoin** 

主要⽤于并⾏计算中，和 MapReduce 原理类似，都是把⼤的计算任务拆分成多个⼩任务并⾏计算。 

 } 

 System.out.print("consume.."); 

 } 

 } 

} 

public static void main(String[] args) { 

 for (int i = 0; i < 2; i++) { 

 Producer producer = new Producer(); 

 producer.start(); 

 } 

 for (int i = 0; i < 5; i++) { 

 Consumer consumer = new Consumer(); 

 consumer.start(); 

 } 

 for (int i = 0; i < 3; i++) { 

 Producer producer = new Producer(); 

 producer.start(); 

 } 

} 

produce..produce..consume..consume..produce..consume..produce..consume..pro 

duce..consume.. 

public class ForkJoinExample extends RecursiveTask<Integer> { 

 private final int threshold = 5; 

 private int first; 

 private int last; 

 public ForkJoinExample(int first, int last) { 

 this.first = first; 

 this.last = last; 

 } 

 @Override 

 protected Integer compute() { 

 int result = 0; if (last - first <= threshold) { 

 // 任务⾜够⼩则直接计算 

 for (int i = first; i <= last; i++) { 

 result += i; 

 } 

 } else { 

 // 拆分成⼩任务 

 int middle = first + (last - first) / 2; 

 ForkJoinExample leftTask = new ForkJoinExample(first, middle); 

 ForkJoinExample rightTask = new ForkJoinExample(middle + 1, 

last); 

 leftTask.fork(); 

 rightTask.fork(); 

 result = leftTask.join() + rightTask.join(); 

 } 

 return result; 

 } 

} 

public static void main(String[] args) throws ExecutionException, 

InterruptedException { 

 ForkJoinExample example = new ForkJoinExample(1, 10000); 

 ForkJoinPool forkJoinPool = new ForkJoinPool(); 

 Future result = forkJoinPool.submit(example); 

 System.out.println(result.get()); 

} 

ForkJoin 使⽤ ForkJoinPool 来启动，它是⼀个特殊的线程池，线程数量取决于 CPU 核数。 

public class ForkJoinPool extends AbstractExecutorService 

ForkJoinPool 实现了⼯作窃取算法来提⾼ CPU 的利⽤率。每个线程都维护了⼀个双端队列，⽤来存储 

需要执⾏的任务。⼯作窃取算法允许空闲的线程从其它线程的双端队列中窃取⼀个任务来执⾏。窃取的 

任务必须是最晚的任务，避免和队列所属线程发⽣竞争。例如下图中，Thread2 从 Thread1 的队列中拿 

出最晚的 Task1 任务，Thread1 会拿出 Task2 来执⾏，这样就避免发⽣竞争。但是如果队列中只有⼀个 

任务时还是会发⽣竞争。九、线程不安全示例 

如果多个线程对同⼀个共享数据进⾏访问⽽不采取同步操作的话，那么操作的结果是不⼀致的。 

以下代码演示了 1000 个线程同时对 cnt 执⾏⾃增操作，操作结束之后它的值有可能⼩于 1000。 

public class ThreadUnsafeExample { 

 private int cnt = 0; 

 public void add() { 

 cnt++; 

 } 

 public int get() { 

 return cnt; 

 } 

} 

public static void main(String[] args) throws InterruptedException { 

 final int threadSize = 1000; 

 ThreadUnsafeExample example = new ThreadUnsafeExample(); 

 final CountDownLatch countDownLatch = new CountDownLatch(threadSize); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < threadSize; i++) { 

 executorService.execute(() -> { 

 example.add(); 

 countDownLatch.countDown(); 

 }); 

 } 

 countDownLatch.await(); 

 executorService.shutdown(); 

 System.out.println(example.get()); 

}⼗、**Java** 内存模型 

Java 内存模型试图屏蔽各种硬件和操作系统的内存访问差异，以实现让 Java 程序在各种平台下都能达 

到⼀致的内存访问效果。 

主内存与⼯作内存 

处理器上的寄存器的读写的速度⽐内存快⼏个数量级，为了解决这种速度⽭盾，在它们之间加⼊了⾼速 

缓存。 

加⼊⾼速缓存带来了⼀个新的问题：缓存⼀致性。如果多个缓存共享同⼀块主内存区域，那么多个缓存 

的数据可能会不⼀致，需要⼀些协议来解决这个问题。 

所有的变量都存储在主内存中，每个线程还有⾃⼰的⼯作内存，⼯作内存存储在⾼速缓存或者寄存器 

中，保存了该线程使⽤的变量的主内存副本拷⻉。 

线程只能直接操作⼯作内存中的变量，不同线程之间的变量值传递需要通过主内存来完成。 

997内存间交互操作 

Java 内存模型定义了 8 个操作来完成主内存和⼯作内存的交互操作。 

read：把⼀个变量的值从主内存传输到⼯作内存中 

load：在 read 之后执⾏，把 read 得到的值放⼊⼯作内存的变量副本中 

use：把⼯作内存中⼀个变量的值传递给执⾏引擎 

assign：把⼀个从执⾏引擎接收到的值赋给⼯作内存的变量 

store：把⼯作内存的⼀个变量的值传送到主内存中 

write：在 store 之后执⾏，把 store 得到的值放⼊主内存的变量中 

lock：作⽤于主内存的变量 

unlock 

内存模型三⼤特性 

\1. 原⼦性 

Java 内存模型保证了 read、load、use、assign、store、write、lock 和 unlock 操作具有原⼦性，例如 

对⼀个 int 类型的变量执⾏ assign 赋值操作，这个操作就是原⼦性的。但是 Java 内存模型允许虚拟机 

将没有被 volatile 修饰的 64 位数据（long，double）的读写操作划分为两次 32 位的操作来进⾏，即 

load、store、read 和 write 操作可以不具备原⼦性。有⼀个错误认识就是，int 等原⼦性的类型在多线程环境中不会出现线程安全问题。前⾯的线程不安全示 

例代码中，cnt 属于 int 类型变量，1000 个线程对它进⾏⾃增操作之后，得到的值为 997 ⽽不是 

1000。 

为了⽅便讨论，将内存间的交互操作简化为 3 个：load、assign、store。 

下图演示了两个线程同时对 cnt 进⾏操作，load、assign、store 这⼀系列操作整体上看不具备原⼦性， 

那么在 T1 修改 cnt 并且还没有将修改后的值写⼊主内存，T2 依然可以读⼊旧值。可以看出，这两个线 

程虽然执⾏了两次⾃增运算，但是主内存中 cnt 的值最后为 1 ⽽不是 2。因此对 int 类型读写操作满⾜ 

原⼦性只是说明 load、assign、store 这些单个操作具备原⼦性。 

AtomicInteger 能保证多个线程修改的原⼦性。使⽤ AtomicInteger 重写之前线程不安全的代码之后得到以下线程安全实现： 

public class AtomicExample { 

 private AtomicInteger cnt = new AtomicInteger(); 

 public void add() { 

 cnt.incrementAndGet(); 

 } 

 public int get() { 

 return cnt.get(); 

 } 

} 

public static void main(String[] args) throws InterruptedException { 

 final int threadSize = 1000; 

 AtomicExample example = new AtomicExample(); // 只修改这条语句 

 final CountDownLatch countDownLatch = new CountDownLatch(threadSize); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < threadSize; i++) { executorService.execute(() -> { 

 example.add(); 

 countDownLatch.countDown(); 

 }); 

 } 

 countDownLatch.await(); 

 executorService.shutdown(); 

 System.out.println(example.get()); 

} 

1000 

除了使⽤原⼦类之外，也可以使⽤ synchronized 互斥锁来保证操作的原⼦性。它对应的内存间交互操 

作为：lock 和 unlock，在虚拟机实现上对应的字节码指令为 monitorenter 和 monitorexit。 

public class AtomicSynchronizedExample { 

 private int cnt = 0; 

 public synchronized void add() { 

 cnt++; 

 } 

 public synchronized int get() { 

 return cnt; 

 } 

} 

public static void main(String[] args) throws InterruptedException { 

 final int threadSize = 1000; 

 AtomicSynchronizedExample example = new AtomicSynchronizedExample(); 

 final CountDownLatch countDownLatch = new CountDownLatch(threadSize); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 for (int i = 0; i < threadSize; i++) { 

 executorService.execute(() -> { 

 example.add(); 

 countDownLatch.countDown(); 

 }); 

 } 

 countDownLatch.await(); 

 executorService.shutdown(); 

 System.out.println(example.get()); 

}2. 可⻅性 

可⻅性指当⼀个线程修改了共享变量的值，其它线程能够⽴即得知这个修改。Java 内存模型是通过在变 

量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值来实现可⻅性的。 

主要有三种实现可⻅性的⽅式： 

volatile 

synchronized，对⼀个变量执⾏ unlock 操作之前，必须把变量值同步回主内存。 

final，被 final 关键字修饰的字段在构造器中⼀旦初始化完成，并且没有发⽣ this 逃逸（其它线程通 

过 this 引⽤访问到初始化了⼀半的对象），那么其它线程就能看⻅ final 字段的值。 

对前⾯的线程不安全示例中的 cnt 变量使⽤ volatile 修饰，不能解决线程不安全问题，因为 volatile 并不 

能保证操作的原⼦性。 

\3. 有序性 

有序性是指：在本线程内观察，所有操作都是有序的。在⼀个线程观察另⼀个线程，所有操作都是⽆序 

的，⽆序是因为发⽣了指令重排序。在 Java 内存模型中，允许编译器和处理器对指令进⾏重排序，重 

排序过程不会影响到单线程程序的执⾏，却会影响到多线程并发执⾏的正确性。 

volatile 关键字通过添加内存屏障的⽅式来禁⽌指令重排，即重排序时不能把后⾯的指令放到内存屏障 

之前。 

也可以通过 synchronized 来保证有序性，它保证每个时刻只有⼀个线程执⾏同步代码，相当于是让线 

程顺序执⾏同步代码。 

先⾏发⽣原则 

上⾯提到了可以⽤ volatile 和 synchronized 来保证有序性。除此之外，JVM 还规定了先⾏发⽣原则， 

让⼀个操作⽆需控制就能先于另⼀个操作完成。 

\1. 单⼀线程原则 

Single Thread rule 

在⼀个线程内，在程序前⾯的操作先⾏发⽣于后⾯的操作。 

\10002. 管程锁定规则 

Monitor Lock Rule 

⼀个 unlock 操作先⾏发⽣于后⾯对同⼀个锁的 lock 操作。 

\3. volatile 变量规则 

Volatile Variable Rule 

对⼀个 volatile 变量的写操作先⾏发⽣于后⾯对这个变量的读操作。4. 线程启动规则 

Thread Start Rule 

Thread 对象的 start() ⽅法调⽤先⾏发⽣于此线程的每⼀个动作。 

\5. 线程加⼊规则 

Thread Join Rule 

Thread 对象的结束先⾏发⽣于 join() ⽅法返回。6. 线程中断规则 

Thread Interruption Rule 

对线程 interrupt() ⽅法的调⽤先⾏发⽣于被中断线程的代码检测到中断事件的发⽣，可以通过 

interrupted() ⽅法检测到是否有中断发⽣。 

\7. 对象终结规则 

Finalizer Rule 

⼀个对象的初始化完成（构造函数执⾏结束）先⾏发⽣于它的 finalize() ⽅法的开始。 

\8. 传递性 

Transitivity 

如果操作 A 先⾏发⽣于操作 B，操作 B 先⾏发⽣于操作 C，那么操作 A 先⾏发⽣于操作 C。 

⼗⼀、线程安全 

多个线程不管以何种⽅式访问某个类，并且在主调代码中不需要进⾏同步，都能表现正确的⾏为。 

线程安全有以下⼏种实现⽅式： 

不可变 

不可变（Immutable）的对象⼀定是线程安全的，不需要再采取任何的线程安全保障措施。只要⼀个不 

可变的对象被正确地构建出来，永远也不会看到它在多个线程之中处于不⼀致的状态。多线程环境下， 

应当尽量使对象成为不可变，来满⾜线程安全。 

不可变的类型： 

final 关键字修饰的基本数据类型 

String 

枚举类型 

Number 部分⼦类，如 Long 和 Double 等数值包装类型，BigInteger 和 BigDecimal 等⼤数据类 

型。但同为 Number 的原⼦类 AtomicInteger 和 AtomicLong 则是可变的。 

对于集合类型，可以使⽤ Collections.unmodifiableXXX() ⽅法来获取⼀个不可变的集合。Collections.unmodifiableXXX() 先对原始的集合进⾏拷⻉，需要对集合进⾏修改的⽅法都直接抛出异 

常。 

互斥同步 

synchronized 和 ReentrantLock。 

⾮阻塞同步 

互斥同步最主要的问题就是线程阻塞和唤醒所带来的性能问题，因此这种同步也称为阻塞同步。 

互斥同步属于⼀种悲观的并发策略，总是认为只要不去做正确的同步措施，那就肯定会出现问题。⽆论 

共享数据是否真的会出现竞争，它都要进⾏加锁（这⾥讨论的是概念模型，实际上虚拟机会优化掉很⼤ 

⼀部分不必要的加锁）、⽤户态核⼼态转换、维护锁计数器和检查是否有被阻塞的线程需要唤醒等操 

作。 

随着硬件指令集的发展，我们可以使⽤基于冲突检测的乐观并发策略：先进⾏操作，如果没有其它线程 

争⽤共享数据，那操作就成功了，否则采取补偿措施（不断地重试，直到成功为⽌）。这种乐观的并发 

策略的许多实现都不需要将线程阻塞，因此这种同步操作称为⾮阻塞同步。 

\1. CAS 

乐观锁需要操作和冲突检测这两个步骤具备原⼦性，这⾥就不能再使⽤互斥同步来保证了，只能靠硬件 

来完成。硬件⽀持的原⼦性操作最典型的是：⽐较并交换（Compare-and-Swap，CAS）。CAS 指令 

需要有 3 个操作数，分别是内存地址 V、旧的预期值 A 和新值 B。当执⾏操作时，只有当 V 的值等于 

A，才将 V 的值更新为 B。 

public class ImmutableExample { 

 public static void main(String[] args) { 

 Map<String, Integer> map = new HashMap<>(); 

 Map<String, Integer> unmodifiableMap = 

Collections.unmodifiableMap(map); 

 unmodifiableMap.put("a", 1); 

 } 

} 

Exception in thread "main" java.lang.UnsupportedOperationException 

 at java.util.Collections$UnmodifiableMap.put(Collections.java:1457) 

 at ImmutableExample.main(ImmutableExample.java:9) 

public V put(K key, V value) { 

 throw new UnsupportedOperationException(); 

}2. AtomicInteger 

J.U.C 包⾥⾯的整数原⼦类 AtomicInteger 的⽅法调⽤了 Unsafe 类的 CAS 操作。 

以下代码使⽤了 AtomicInteger 执⾏了⾃增的操作。 

以下代码是 incrementAndGet() 的源码，它调⽤了 Unsafe 的 getAndAddInt() 。 

以下代码是 getAndAddInt() 源码，var1 指示对象内存地址，var2 指示该字段相对对象内存地址的偏 

移，var4 指示操作需要加的数值，这⾥为 1。通过 getIntVolatile(var1, var2) 得到旧的预期值，通过调⽤ 

compareAndSwapInt() 来进⾏ CAS ⽐较，如果该字段内存地址中的值等于 var5，那么就更新内存地址 

为 var1+var2 的变量为 var5+var4。 

可以看到 getAndAddInt() 在⼀个循环中进⾏，发⽣冲突的做法是不断的进⾏重试。 

\3. ABA 

如果⼀个变量初次读取的时候是 A 值，它的值被改成了 B，后来⼜被改回为 A，那 CAS 操作就会误认 

为它从来没有被改变过。 

J.U.C 包提供了⼀个带有标记的原⼦引⽤类 AtomicStampedReference 来解决这个问题，它可以通过控 

制变量值的版本来保证 CAS 的正确性。⼤部分情况下 ABA 问题不会影响程序并发的正确性，如果需要 

解决 ABA 问题，改⽤传统的互斥同步可能会⽐原⼦类更⾼效。 

private AtomicInteger cnt = new AtomicInteger(); 

public void add() { 

 cnt.incrementAndGet(); 

} 

public final int incrementAndGet() { 

 return unsafe.getAndAddInt(this, valueOffset, 1) + 1; 

} 

public final int getAndAddInt(Object var1, long var2, int var4) { 

 int var5; 

 do { 

 var5 = this.getIntVolatile(var1, var2); 

 } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4)); 

 return var5; 

}⽆同步⽅案 

要保证线程安全，并不是⼀定就要进⾏同步。如果⼀个⽅法本来就不涉及共享数据，那它⾃然就⽆须任 

何同步措施去保证正确性。 

\1. 栈封闭 

多个线程访问同⼀个⽅法的局部变量时，不会出现线程安全问题，因为局部变量存储在虚拟机栈中，属 

于线程私有的。 

\2. 线程本地存储（Thread Local Storage） 

如果⼀段代码中所需要的数据必须与其他代码共享，那就看看这些共享数据的代码是否能保证在同⼀个 

线程中执⾏。如果能保证，我们就可以把共享数据的可⻅范围限制在同⼀个线程之内，这样，⽆须同步 

也能保证线程之间不出现数据争⽤的问题。 

符合这种特点的应⽤并不少⻅，⼤部分使⽤消费队列的架构模式（如“⽣产者-消费者”模式）都会将产品 

的消费过程尽量在⼀个线程中消费完。其中最重要的⼀个应⽤实例就是经典 Web 交互模型中的“⼀个请 

求对应⼀个服务器线程”（Thread-per-Request）的处理⽅式，这种处理⽅式的⼴泛应⽤使得很多 Web 

服务端应⽤都可以使⽤线程本地存储来解决线程安全问题。 

可以使⽤ java.lang.ThreadLocal 类来实现线程本地存储功能。 

public class StackClosedExample { 

 public void add100() { 

 int cnt = 0; 

 for (int i = 0; i < 100; i++) { 

 cnt++; 

 } 

 System.out.println(cnt); 

 } 

} 

public static void main(String[] args) { 

 StackClosedExample example = new StackClosedExample(); 

 ExecutorService executorService = Executors.newCachedThreadPool(); 

 executorService.execute(() -> example.add100()); 

 executorService.execute(() -> example.add100()); 

 executorService.shutdown(); 

} 

100 

100对于以下代码，thread1 中设置 threadLocal 为 1，⽽ thread2 设置 threadLocal 为 2。过了⼀段时间之 

后，thread1 读取 threadLocal 依然是 1，不受 thread2 的影响。 

public class ThreadLocalExample { 

 public static void main(String[] args) { 

 ThreadLocal threadLocal = new ThreadLocal(); 

 Thread thread1 = new Thread(() -> { 

 threadLocal.set(1); 

 try { 

 Thread.sleep(1000); 

 } catch (InterruptedException e) { 

 e.printStackTrace(); 

 } 

 System.out.println(threadLocal.get()); 

 threadLocal.remove(); 

 }); 

 Thread thread2 = new Thread(() -> { 

 threadLocal.set(2); 

 threadLocal.remove(); 

 }); 

 thread1.start(); 

 thread2.start(); 

 } 

}

1 

为了理解 ThreadLocal，先看以下代码： 

public class ThreadLocalExample1 { 

 public static void main(String[] args) { 

 ThreadLocal threadLocal1 = new ThreadLocal(); 

 ThreadLocal threadLocal2 = new ThreadLocal(); 

 Thread thread1 = new Thread(() -> { 

 threadLocal1.set(1); 

 threadLocal2.set(1); 

 }); 

 Thread thread2 = new Thread(() -> { 

 threadLocal1.set(2); 

 threadLocal2.set(2); 

 }); 

 thread1.start(); thread2.start(); 

 } 

} 

它所对应的底层结构图为： 

每个 Thread 都有⼀个 ThreadLocal.ThreadLocalMap 对象。 

/* ThreadLocal values pertaining to this thread. This map is maintained 

\* by the ThreadLocal class. */ 

ThreadLocal.ThreadLocalMap threadLocals = null; 

当调⽤⼀个 ThreadLocal 的 set(T value) ⽅法时，先得到当前线程的 ThreadLocalMap 对象，然后将 

ThreadLocal->value 键值对插⼊到该 Map 中。 

public void set(T value) { 

 Thread t = Thread.currentThread(); 

 ThreadLocalMap map = getMap(t); 

 if (map != null) 

 map.set(this, value); 

 else 

 createMap(t, value); 

} 

get() ⽅法类似。ThreadLocal 从理论上讲并不是⽤来解决多线程并发问题的，因为根本不存在多线程竞争。 

在⼀些场景 (尤其是使⽤线程池) 下，由于 ThreadLocal.ThreadLocalMap 的底层数据结构导致 

ThreadLocal 有内存泄漏的情况，应该尽可能在每次使⽤ ThreadLocal 后⼿动调⽤ remove()，以避免出 

现 ThreadLocal 经典的内存泄漏甚⾄是造成⾃身业务混乱的⻛险。 

\3. 可重⼊代码（Reentrant Code） 

这种代码也叫做纯代码（Pure Code），可以在代码执⾏的任何时刻中断它，转⽽去执⾏另外⼀段代码 

（包括递归调⽤它本身），⽽在控制权返回后，原来的程序不会出现任何错误。 

可重⼊代码有⼀些共同的特征，例如不依赖存储在堆上的数据和公⽤的系统资源、⽤到的状态量都由参 

数中传⼊、不调⽤⾮可重⼊的⽅法等。 

⼗⼆、锁优化 

这⾥的锁优化主要是指 JVM 对 synchronized 的优化。 

⾃旋锁 

互斥同步进⼊阻塞状态的开销都很⼤，应该尽量避免。在许多应⽤中，共享数据的锁定状态只会持续很 

短的⼀段时间。⾃旋锁的思想是让⼀个线程在请求⼀个共享数据的锁时执⾏忙循环（⾃旋）⼀段时间， 

如果在这段时间内能获得锁，就可以避免进⼊阻塞状态。 

⾃旋锁虽然能避免进⼊阻塞状态从⽽减少开销，但是它需要进⾏忙循环操作占⽤ CPU 时间，它只适⽤ 

于共享数据的锁定状态很短的场景。 

在 JDK 1.6 中引⼊了⾃适应的⾃旋锁。⾃适应意味着⾃旋的次数不再固定了，⽽是由前⼀次在同⼀个锁 

上的⾃旋次数及锁的拥有者的状态来决定。 

锁消除 

public T get() { 

 Thread t = Thread.currentThread(); 

 ThreadLocalMap map = getMap(t); 

 if (map != null) { 

 ThreadLocalMap.Entry e = map.getEntry(this); 

 if (e != null) { 

 @SuppressWarnings("unchecked") 

 T result = (T)e.value; 

 return result; 

 } 

 } 

 return setInitialValue(); 

}锁消除是指对于被检测出不可能存在竞争的共享数据的锁进⾏消除。 

锁消除主要是通过逃逸分析来⽀持，如果堆上的共享数据不可能逃逸出去被其它线程访问到，那么就可 

以把它们当成私有数据对待，也就可以将它们的锁进⾏消除。 

对于⼀些看起来没有加锁的代码，其实隐式的加了很多锁。例如下⾯的字符串拼接代码就隐式加了锁： 

String 是⼀个不可变的类，编译器会对 String 的拼接⾃动优化。在 JDK 1.5 之前，会转化为 

StringBuffer 对象的连续 append() 操作： 

每个 append() ⽅法中都有⼀个同步块。虚拟机观察变量 sb，很快就会发现它的动态作⽤域被限制在 

concatString() ⽅法内部。也就是说，sb 的所有引⽤永远不会逃逸到 concatString() ⽅法之外，其他线 

程⽆法访问到它，因此可以进⾏消除。 

锁粗化 

如果⼀系列的连续操作都对同⼀个对象反复加锁和解锁，频繁的加锁操作就会导致性能损耗。 

上⼀节的示例代码中连续的 append() ⽅法就属于这类情况。如果虚拟机探测到由这样的⼀串零碎的操 

作都对同⼀个对象加锁，将会把加锁的范围扩展（粗化）到整个操作序列的外部。对于上⼀节的示例代 

码就是扩展到第⼀个 append() 操作之前直⾄最后⼀个 append() 操作之后，这样只需要加锁⼀次就可以 

了。 

轻量级锁 

JDK 1.6 引⼊了偏向锁和轻量级锁，从⽽让锁拥有了四个状态：⽆锁状态（unlocked）、偏向锁状态 

（biasble）、轻量级锁状态（lightweight locked）和重量级锁状态（inflated）。 

以下是 HotSpot 虚拟机对象头的内存布局，这些数据被称为 Mark Word。其中 tag bits 对应了五个状 

态，这些状态在右侧的 state 表格中给出。除了 marked for gc 状态，其它四个状态已经在前⾯介绍过 

了。 

public static String concatString(String s1, String s2, String s3) { 

 return s1 + s2 + s3; 

} 

public static String concatString(String s1, String s2, String s3) { 

 StringBuffer sb = new StringBuffer(); 

 sb.append(s1); 

 sb.append(s2); 

 sb.append(s3); 

 return sb.toString(); 

}下图左侧是⼀个线程的虚拟机栈，其中有⼀部分称为 Lock Record 的区域，这是在轻量级锁运⾏过程创 

建的，⽤于存放锁对象的 Mark Word。⽽右侧就是⼀个锁对象，包含了 Mark Word 和其它信息。 

轻量级锁是相对于传统的重量级锁⽽⾔，它使⽤ CAS 操作来避免重量级锁使⽤互斥量的开销。对于绝 

⼤部分的锁，在整个同步周期内都是不存在竞争的，因此也就不需要都使⽤互斥量进⾏同步，可以先采 

⽤ CAS 操作进⾏同步，如果 CAS 失败了再改⽤互斥量进⾏同步。 

当尝试获取⼀个锁对象时，如果锁对象标记为 0 01，说明锁对象的锁未锁定（unlocked）状态。此时虚 

拟机在当前线程的虚拟机栈中创建 Lock Record，然后使⽤ CAS 操作将对象的 Mark Word 更新为 

Lock Record 指针。如果 CAS 操作成功了，那么线程就获取了该对象上的锁，并且对象的 Mark Word 

的锁标记变为 00，表示该对象处于轻量级锁状态。如果 CAS 操作失败了，虚拟机⾸先会检查对象的 Mark Word 是否指向当前线程的虚拟机栈，如果是的 

话说明当前线程已经拥有了这个锁对象，那就可以直接进⼊同步块继续执⾏，否则说明这个锁对象已经 

被其他线程线程抢占了。如果有两条以上的线程争⽤同⼀个锁，那轻量级锁就不再有效，要膨胀为重量 

级锁。 

偏向锁 

偏向锁的思想是偏向于让第⼀个获取锁对象的线程，这个线程在之后获取该锁就不再需要进⾏同步操 

作，甚⾄连 CAS 操作也不再需要。 

当锁对象第⼀次被线程获得的时候，进⼊偏向状态，标记为 1 01。同时使⽤ CAS 操作将线程 ID 记录到 

Mark Word 中，如果 CAS 操作成功，这个线程以后每次进⼊这个锁相关的同步块就不需要再进⾏任何 

同步操作。 

当有另外⼀个线程去尝试获取这个锁对象时，偏向状态就宣告结束，此时撤销偏向（Revoke Bias）后 

恢复到未锁定状态或者轻量级锁状态。⼗三、多线程开发良好的实践 

给线程起个有意义的名字，这样可以⽅便找 Bug。 

缩⼩同步范围，从⽽减少锁争⽤。例如对于 synchronized，应该尽量使⽤同步块⽽不是同步⽅法。 

多⽤同步⼯具少⽤ wait() 和 notify()。⾸先，CountDownLatch, CyclicBarrier, Semaphore 和 

Exchanger 这些同步类简化了编码操作，⽽⽤ wait() 和 notify() 很难实现复杂控制流；其次，这些同 

步类是由最好的企业编写和维护，在后续的 JDK 中还会不断优化和完善。 

使⽤ BlockingQueue 实现⽣产者消费者问题。 

多⽤并发集合少⽤同步集合，例如应该使⽤ ConcurrentHashMap ⽽不是 Hashtable。 

使⽤本地变量和不可变类来保证线程安全。 

使⽤线程池⽽不是直接创建线程，这是因为创建线程代价很⾼，线程池可以有效地利⽤有限的线程 

来启动任务。 

参考资料 

BruceEckel. Java 编程思想: 第 4 版 [M]. 机械⼯业出版社, 2007. 

周志明. 深⼊理解 Java 虚拟机 [M]. 机械⼯业出版社, 2011. 

Threads and Locks 

线程通信 

Java 线程⾯试题 Top 50 

BlockingQueue 

thread state java 

CSC 456 Spring 2012/ch7 MN 

Java - Understanding Happens-before relationship 

6장 Thread Synchronization 

How is Java's ThreadLocal implemented under the hood? 

Concurrent 

JAVA FORK JOIN EXAMPLE 

聊聊并发（⼋）——Fork/Join 框架介绍 

Eliminating SynchronizationRelated Atomic Operations with Biased Locking and Bulk Rebiasing 

Java 虚拟机 

Java 虚拟机 

⼀、运⾏时数据区域 

程序计数器 

Java 虚拟机栈 

本地⽅法栈堆 

⽅法区 

运⾏时常量池 

直接内存 

⼆、垃圾收集 

判断⼀个对象是否可被回收 

引⽤类型 

垃圾收集算法 

垃圾收集器 

三、内存分配与回收策略 

Minor GC 和 Full GC 

内存分配策略 

Full GC 的触发条件 

四、类加载机制 

类的⽣命周期 

类加载过程 

类初始化时机 

类与类加载器 

类加载器分类 

双亲委派模型 

⾃定义类加载器实现 

参考资料 

本⽂⼤部分内容参考 周志明《深⼊理解 **Java** 虚拟机》 ，想要深⼊学习的话请看原书。 

⼀、运⾏时数据区域程序计数器 

记录正在执⾏的虚拟机字节码指令的地址（如果正在执⾏的是本地⽅法则为空）。 

**Java** 虚拟机栈 

每个 Java ⽅法在执⾏的同时会创建⼀个栈帧⽤于存储局部变量表、操作数栈、常量池引⽤等信息。从 

⽅法调⽤直⾄执⾏完成的过程，对应着⼀个栈帧在 Java 虚拟机栈中⼊栈和出栈的过程。可以通过 -Xss 这个虚拟机参数来指定每个线程的 Java 虚拟机栈内存⼤⼩，在 JDK 1.4 中默认为 

256K，⽽在 JDK 1.5+ 默认为 1M： 

该区域可能抛出以下异常： 

当线程请求的栈深度超过最⼤值，会抛出 StackOverflowError 异常； 

栈进⾏动态扩展时如果⽆法申请到⾜够内存，会抛出 OutOfMemoryError 异常。 

本地⽅法栈 

本地⽅法栈与 Java 虚拟机栈类似，它们之间的区别只不过是本地⽅法栈为本地⽅法服务。 

本地⽅法⼀般是⽤其它语⾔（C、C++ 或汇编语⾔等）编写的，并且被编译为基于本机硬件和操作系统 

的程序，对待这些⽅法需要特别处理。 

堆 

所有对象都在这⾥分配内存，是垃圾收集的主要区域（"GC 堆"）。 

现代的垃圾收集器基本都是采⽤分代收集算法，其主要的思想是针对不同类型的对象采取不同的垃圾回 

收算法。可以将堆分成两块： 

java -Xss2M HackTheJava新⽣代（Young Generation） 

⽼年代（Old Generation） 

堆不需要连续内存，并且可以动态增加其内存，增加失败会抛出 OutOfMemoryError 异常。 

可以通过 -Xms 和 -Xmx 这两个虚拟机参数来指定⼀个程序的堆内存⼤⼩，第⼀个参数设置初始值，第 

⼆个参数设置最⼤值。 

⽅法区 

⽤于存放已被加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。 

和堆⼀样不需要连续的内存，并且可以动态扩展，动态扩展失败⼀样会抛出 OutOfMemoryError 异常。 

对这块区域进⾏垃圾回收的主要⽬标是对常量池的回收和对类的卸载，但是⼀般⽐较难实现。 

HotSpot 虚拟机把它当成永久代来进⾏垃圾回收。但很难确定永久代的⼤⼩，因为它受到很多因素影 

响，并且每次 Full GC 之后永久代的⼤⼩都会改变，所以经常会抛出 OutOfMemoryError 异常。为了更 

容易管理⽅法区，从 JDK 1.8 开始，移除永久代，并把⽅法区移⾄元空间，它位于本地内存中，⽽不是 

虚拟机内存中。 

⽅法区是⼀个 JVM 规范，永久代与元空间都是其⼀种实现⽅式。在 JDK 1.8 之后，原来永久代的数据 

被分到了堆和元空间中。元空间存储类的元信息，静态变量和常量池等放⼊堆中。 

运⾏时常量池 

运⾏时常量池是⽅法区的⼀部分。 

Class ⽂件中的常量池（编译器⽣成的字⾯量和符号引⽤）会在类加载后被放⼊这个区域。 

除了在编译期⽣成的常量，还允许动态⽣成，例如 String 类的 intern()。 

直接内存 

在 JDK 1.4 中新引⼊了 NIO 类，它可以使⽤ Native 函数库直接分配堆外内存，然后通过 Java 堆⾥的 

DirectByteBuffer 对象作为这块内存的引⽤进⾏操作。这样能在⼀些场景中显著提⾼性能，因为避免了 

在堆内存和堆外内存来回拷⻉数据。 

⼆、垃圾收集 

垃圾收集主要是针对堆和⽅法区进⾏。程序计数器、虚拟机栈和本地⽅法栈这三个区域属于线程私有 

的，只存在于线程的⽣命周期内，线程结束之后就会消失，因此不需要对这三个区域进⾏垃圾回收。 

java -Xms1M -Xmx2M HackTheJava判断⼀个对象是否可被回收 

\1. 引⽤计数算法 

为对象添加⼀个引⽤计数器，当对象增加⼀个引⽤时计数器加 1，引⽤失效时计数器减 1。引⽤计数为 

0 的对象可被回收。 

在两个对象出现循环引⽤的情况下，此时引⽤计数器永远不为 0，导致⽆法对它们进⾏回收。正是因为 

循环引⽤的存在，因此 Java 虚拟机不使⽤引⽤计数算法。 

在上述代码中，a 与 b 引⽤的对象实例互相持有了对象的引⽤，因此当我们把对 a 对象与 b 对象的引⽤ 

去除之后，由于两个对象还存在互相之间的引⽤，导致两个 Test 对象⽆法被回收。 

\2. 可达性分析算法 

以 GC Roots 为起始点进⾏搜索，可达的对象都是存活的，不可达的对象可被回收。 

Java 虚拟机使⽤该算法来判断对象是否可被回收，GC Roots ⼀般包含以下内容： 

虚拟机栈中局部变量表中引⽤的对象 

本地⽅法栈中 JNI 中引⽤的对象 

⽅法区中类静态属性引⽤的对象 

⽅法区中的常量引⽤的对象 

public class Test { 

 public Object instance = null; 

 public static void main(String[] args) { 

 Test a = new Test(); 

 Test b = new Test(); 

 a.instance = b; 

 b.instance = a; 

 a = null; 

 b = null; 

 doSomething(); 

 } 

}3. ⽅法区的回收 

因为⽅法区主要存放永久代对象，⽽永久代对象的回收率⽐新⽣代低很多，所以在⽅法区上进⾏回收性 

价⽐不⾼。 

主要是对常量池的回收和对类的卸载。 

为了避免内存溢出，在⼤量使⽤反射和动态代理的场景都需要虚拟机具备类卸载功能。 

类的卸载条件很多，需要满⾜以下三个条件，并且满⾜了条件也不⼀定会被卸载： 

该类所有的实例都已经被回收，此时堆中不存在该类的任何实例。 

加载该类的 ClassLoader 已经被回收。 

该类对应的 Class 对象没有在任何地⽅被引⽤，也就⽆法在任何地⽅通过反射访问该类⽅法。 

\4. finalize() 

类似 C++ 的析构函数，⽤于关闭外部资源。但是 try-finally 等⽅式可以做得更好，并且该⽅法运⾏代价 

很⾼，不确定性⼤，⽆法保证各个对象的调⽤顺序，因此最好不要使⽤。 

当⼀个对象可被回收时，如果需要执⾏该对象的 finalize() ⽅法，那么就有可能在该⽅法中让对象重新被 

引⽤，从⽽实现⾃救。⾃救只能进⾏⼀次，如果回收的对象之前调⽤了 finalize() ⽅法⾃救，后⾯回收时 

不会再调⽤该⽅法。 

引⽤类型 

⽆论是通过引⽤计数算法判断对象的引⽤数量，还是通过可达性分析算法判断对象是否可达，判定对象 

是否可被回收都与引⽤有关。 

Java 提供了四种强度不同的引⽤类型。 

\1. 强引⽤ 

被强引⽤关联的对象不会被回收。使⽤ new ⼀个新对象的⽅式来创建强引⽤。 

\2. 软引⽤ 

被软引⽤关联的对象只有在内存不够的情况下才会被回收。 

使⽤ SoftReference 类来创建软引⽤。 

\3. 弱引⽤ 

被弱引⽤关联的对象⼀定会被回收，也就是说它只能存活到下⼀次垃圾回收发⽣之前。 

使⽤ WeakReference 类来创建弱引⽤。 

\4. 虚引⽤ 

⼜称为幽灵引⽤或者幻影引⽤，⼀个对象是否有虚引⽤的存在，不会对其⽣存时间造成影响，也⽆法通 

过虚引⽤得到⼀个对象。 

为⼀个对象设置虚引⽤的唯⼀⽬的是能在这个对象被回收时收到⼀个系统通知。 

使⽤ PhantomReference 来创建虚引⽤。 

垃圾收集算法 

\1. 标记 - 清除 

Object obj = new Object(); 

Object obj = new Object(); 

SoftReference<Object> sf = new SoftReference<Object>(obj); 

obj = null; // 使对象只被软引⽤关联 

Object obj = new Object(); 

WeakReference<Object> wf = new WeakReference<Object>(obj); 

obj = null; 

Object obj = new Object(); 

PhantomReference<Object> pf = new PhantomReference<Object>(obj, null); 

obj = null;在标记阶段，程序会检查每个对象是否为活动对象，如果是活动对象，则程序会在对象头部打上标记。 

在清除阶段，会进⾏对象回收并取消标志位，另外，还会判断回收后的分块与前⼀个空闲分块是否连 

续，若连续，会合并这两个分块。回收对象就是把对象作为分块，连接到被称为 “空闲链表” 的单向链 

表，之后进⾏分配时只需要遍历这个空闲链表，就可以找到分块。 

在分配时，程序会搜索空闲链表寻找空间⼤于等于新对象⼤⼩ size 的块 block。如果它找到的块等于 

size，会直接返回这个分块；如果找到的块⼤于 size，会将块分割成⼤⼩为 size 与 (block - size) 的两部 

分，返回⼤⼩为 size 的分块，并把⼤⼩为 (block - size) 的块返回给空闲链表。 

不⾜： 

标记和清除过程效率都不⾼； 

会产⽣⼤量不连续的内存碎⽚，导致⽆法给⼤对象分配内存。 

\2. 标记 - 整理 

让所有存活的对象都向⼀端移动，然后直接清理掉端边界以外的内存。 

优点: 

不会产⽣内存碎⽚不⾜: 

需要移动⼤量对象，处理效率⽐较低。 

\3. 复制 

将内存划分为⼤⼩相等的两块，每次只使⽤其中⼀块，当这⼀块内存⽤完了就将还存活的对象复制到另 

⼀块上⾯，然后再把使⽤过的内存空间进⾏⼀次清理。 

主要不⾜是只使⽤了内存的⼀半。 

现在的商业虚拟机都采⽤这种收集算法回收新⽣代，但是并不是划分为⼤⼩相等的两块，⽽是⼀块较⼤ 

的 Eden 空间和两块较⼩的 Survivor 空间，每次使⽤ Eden 和其中⼀块 Survivor。在回收时，将 Eden 

和 Survivor 中还存活着的对象全部复制到另⼀块 Survivor 上，最后清理 Eden 和使⽤过的那⼀块 

Survivor。 

HotSpot 虚拟机的 Eden 和 Survivor ⼤⼩⽐例默认为 8:1，保证了内存的利⽤率达到 90%。如果每次回 

收有多于 10% 的对象存活，那么⼀块 Survivor 就不够⽤了，此时需要依赖于⽼年代进⾏空间分配担 

保，也就是借⽤⽼年代的空间存储放不下的对象。 

\4. 分代收集 

现在的商业虚拟机采⽤分代收集算法，它根据对象存活周期将内存划分为⼏块，不同块采⽤适当的收集 

算法。 

⼀般将堆分为新⽣代和⽼年代。 

新⽣代使⽤：复制算法 

⽼年代使⽤：标记 - 清除 或者 标记 - 整理 算法 

垃圾收集器以上是 HotSpot 虚拟机中的 7 个垃圾收集器，连线表示垃圾收集器可以配合使⽤。 

单线程与多线程：单线程指的是垃圾收集器只使⽤⼀个线程，⽽多线程使⽤多个线程； 

串⾏与并⾏：串⾏指的是垃圾收集器与⽤户程序交替执⾏，这意味着在执⾏垃圾收集的时候需要停 

顿⽤户程序；并⾏指的是垃圾收集器和⽤户程序同时执⾏。除了 CMS 和 G1 之外，其它垃圾收集 

器都是以串⾏的⽅式执⾏。 

\1. Serial 收集器 

Serial 翻译为串⾏，也就是说它以串⾏的⽅式执⾏。 

它是单线程的收集器，只会使⽤⼀个线程进⾏垃圾收集⼯作。 

它的优点是简单⾼效，在单个 CPU 环境下，由于没有线程交互的开销，因此拥有最⾼的单线程收集效 

率。它是 Client 场景下的默认新⽣代收集器，因为在该场景下内存⼀般来说不会很⼤。它收集⼀两百兆垃圾 

的停顿时间可以控制在⼀百多毫秒以内，只要不是太频繁，这点停顿时间是可以接受的。 

\2. ParNew 收集器 

它是 Serial 收集器的多线程版本。 

它是 Server 场景下默认的新⽣代收集器，除了性能原因外，主要是因为除了 Serial 收集器，只有它能 

与 CMS 收集器配合使⽤。 

\3. Parallel Scavenge 收集器 

与 ParNew ⼀样是多线程收集器。 

其它收集器⽬标是尽可能缩短垃圾收集时⽤户线程的停顿时间，⽽它的⽬标是达到⼀个可控制的吞吐 

量，因此它被称为“吞吐量优先”收集器。这⾥的吞吐量指 CPU ⽤于运⾏⽤户程序的时间占总时间的⽐ 

值。 

停顿时间越短就越适合需要与⽤户交互的程序，良好的响应速度能提升⽤户体验。⽽⾼吞吐量则可以⾼ 

效率地利⽤ CPU 时间，尽快完成程序的运算任务，适合在后台运算⽽不需要太多交互的任务。 

缩短停顿时间是以牺牲吞吐量和新⽣代空间来换取的：新⽣代空间变⼩，垃圾回收变得频繁，导致吞吐 

量下降。 

可以通过⼀个开关参数打开 GC ⾃适应的调节策略（GC Ergonomics），就不需要⼿⼯指定新⽣代的⼤ 

⼩（-Xmn）、Eden 和 Survivor 区的⽐例、晋升⽼年代对象年龄等细节参数了。虚拟机会根据当前系统 

的运⾏情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间或者最⼤的吞吐量。 

\4. Serial Old 收集器是 Serial 收集器的⽼年代版本，也是给 Client 场景下的虚拟机使⽤。如果⽤在 Server 场景下，它有两 

⼤⽤途： 

在 JDK 1.5 以及之前版本（Parallel Old 诞⽣以前）中与 Parallel Scavenge 收集器搭配使⽤。 

作为 CMS 收集器的后备预案，在并发收集发⽣ Concurrent Mode Failure 时使⽤。 

\5. Parallel Old 收集器 

是 Parallel Scavenge 收集器的⽼年代版本。 

在注重吞吐量以及 CPU 资源敏感的场合，都可以优先考虑 Parallel Scavenge 加 Parallel Old 收集器。 

\6. CMS 收集器CMS（Concurrent Mark Sweep），Mark Sweep 指的是标记 - 清除算法。 

分为以下四个流程： 

初始标记：仅仅只是标记⼀下 GC Roots 能直接关联到的对象，速度很快，需要停顿。 

并发标记：进⾏ GC Roots Tracing 的过程，它在整个回收过程中耗时最⻓，不需要停顿。 

重新标记：为了修正并发标记期间因⽤户程序继续运作⽽导致标记产⽣变动的那⼀部分对象的标记 

记录，需要停顿。 

并发清除：不需要停顿。 

在整个过程中耗时最⻓的并发标记和并发清除过程中，收集器线程都可以与⽤户线程⼀起⼯作，不需要 

进⾏停顿。 

具有以下缺点： 

吞吐量低：低停顿时间是以牺牲吞吐量为代价的，导致 CPU 利⽤率不够⾼。 

⽆法处理浮动垃圾，可能出现 Concurrent Mode Failure。浮动垃圾是指并发清除阶段由于⽤户线程 

继续运⾏⽽产⽣的垃圾，这部分垃圾只能到下⼀次 GC 时才能进⾏回收。由于浮动垃圾的存在，因 

此需要预留出⼀部分内存，意味着 CMS 收集不能像其它收集器那样等待⽼年代快满的时候再回 

收。如果预留的内存不够存放浮动垃圾，就会出现 Concurrent Mode Failure，这时虚拟机将临时启 

⽤ Serial Old 来替代 CMS。 

标记 - 清除算法导致的空间碎⽚，往往出现⽼年代空间剩余，但⽆法找到⾜够⼤连续空间来分配当 

前对象，不得不提前触发⼀次 Full GC。 

\7. G1 收集器 

G1（Garbage-First），它是⼀款⾯向服务端应⽤的垃圾收集器，在多 CPU 和⼤内存的场景下有很好的 

性能。HotSpot 开发团队赋予它的使命是未来可以替换掉 CMS 收集器。 

堆被分为新⽣代和⽼年代，其它收集器进⾏收集的范围都是整个新⽣代或者⽼年代，⽽ G1 可以直接对 

新⽣代和⽼年代⼀起回收。G1 把堆划分成多个⼤⼩相等的独⽴区域（Region），新⽣代和⽼年代不再物理隔离。 

通过引⼊ Region 的概念，从⽽将原来的⼀整块内存空间划分成多个的⼩空间，使得每个⼩空间可以单 

独进⾏垃圾回收。这种划分⽅法带来了很⼤的灵活性，使得可预测的停顿时间模型成为可能。通过记录 

每个 Region 垃圾回收时间以及回收所获得的空间（这两个值是通过过去回收的经验获得），并维护⼀ 

个优先列表，每次根据允许的收集时间，优先回收价值最⼤的 Region。每个 Region 都有⼀个 Remembered Set，⽤来记录该 Region 对象的引⽤对象所在的 Region。通过使 

⽤ Remembered Set，在做可达性分析的时候就可以避免全堆扫描。 

如果不计算维护 Remembered Set 的操作，G1 收集器的运作⼤致可划分为以下⼏个步骤： 

初始标记 

并发标记 

最终标记：为了修正在并发标记期间因⽤户程序继续运作⽽导致标记产⽣变动的那⼀部分标记记 

录，虚拟机将这段时间对象变化记录在线程的 Remembered Set Logs ⾥⾯，最终标记阶段需要把 

Remembered Set Logs 的数据合并到 Remembered Set 中。这阶段需要停顿线程，但是可并⾏执 

⾏。 

筛选回收：⾸先对各个 Region 中的回收价值和成本进⾏排序，根据⽤户所期望的 GC 停顿时间来 

制定回收计划。此阶段其实也可以做到与⽤户程序⼀起并发执⾏，但是因为只回收⼀部分 Region， 

时间是⽤户可控制的，⽽且停顿⽤户线程将⼤幅度提⾼收集效率。 

具备如下特点： 

空间整合：整体来看是基于“标记 - 整理”算法实现的收集器，从局部（两个 Region 之间）上来看是 

基于“复制”算法实现的，这意味着运⾏期间不会产⽣内存空间碎⽚。 

可预测的停顿：能让使⽤者明确指定在⼀个⻓度为 M 毫秒的时间⽚段内，消耗在 GC 上的时间不得 

超过 N 毫秒。 

三、内存分配与回收策略 

**Minor GC** 和 **Full GC** 

Minor GC：回收新⽣代，因为新⽣代对象存活时间很短，因此 Minor GC 会频繁执⾏，执⾏的速度 

⼀般也会⽐较快。 

Full GC：回收⽼年代和新⽣代，⽼年代对象其存活时间⻓，因此 Full GC 很少执⾏，执⾏速度会⽐ 

Minor GC 慢很多。 

内存分配策略 

\1. 对象优先在 Eden 分配⼤多数情况下，对象在新⽣代 Eden 上分配，当 Eden 空间不够时，发起 Minor GC。 

\2. ⼤对象直接进⼊⽼年代 

⼤对象是指需要连续内存空间的对象，最典型的⼤对象是那种很⻓的字符串以及数组。 

经常出现⼤对象会提前触发垃圾收集以获取⾜够的连续空间分配给⼤对象。 

-XX:PretenureSizeThreshold，⼤于此值的对象直接在⽼年代分配，避免在 Eden 和 Survivor 之间的⼤ 

量内存复制。 

\3. ⻓期存活的对象进⼊⽼年代 

为对象定义年龄计数器，对象在 Eden 出⽣并经过 Minor GC 依然存活，将移动到 Survivor 中，年龄就 

增加 1 岁，增加到⼀定年龄则移动到⽼年代中。 

-XX:MaxTenuringThreshold ⽤来定义年龄的阈值。 

\4. 动态对象年龄判定 

虚拟机并不是永远要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升⽼年代，如果在 Survivor 

中相同年龄所有对象⼤⼩的总和⼤于 Survivor 空间的⼀半，则年龄⼤于或等于该年龄的对象可以直接进 

⼊⽼年代，⽆需等到 MaxTenuringThreshold 中要求的年龄。 

\5. 空间分配担保 

在发⽣ Minor GC 之前，虚拟机先检查⽼年代最⼤可⽤的连续空间是否⼤于新⽣代所有对象总空间，如 

果条件成⽴的话，那么 Minor GC 可以确认是安全的。 

如果不成⽴的话虚拟机会查看 HandlePromotionFailure 的值是否允许担保失败，如果允许那么就会继续 

检查⽼年代最⼤可⽤的连续空间是否⼤于历次晋升到⽼年代对象的平均⼤⼩，如果⼤于，将尝试着进⾏ 

⼀次 Minor GC；如果⼩于，或者 HandlePromotionFailure 的值不允许冒险，那么就要进⾏⼀次 Full 

GC。 

**Full GC** 的触发条件 

对于 Minor GC，其触发条件⾮常简单，当 Eden 空间满时，就将触发⼀次 Minor GC。⽽ Full GC 则相 

对复杂，有以下条件： 

\1. 调⽤ System.gc() 

只是建议虚拟机执⾏ Full GC，但是虚拟机不⼀定真正去执⾏。不建议使⽤这种⽅式，⽽是让虚拟机管 

理内存。 

\2. ⽼年代空间不⾜ 

⽼年代空间不⾜的常⻅场景为前⽂所讲的⼤对象直接进⼊⽼年代、⻓期存活的对象进⼊⽼年代等。为了避免以上原因引起的 Full GC，应当尽量不要创建过⼤的对象以及数组。除此之外，可以通过 -Xmn 

虚拟机参数调⼤新⽣代的⼤⼩，让对象尽量在新⽣代被回收掉，不进⼊⽼年代。还可以通过 - 

XX:MaxTenuringThreshold 调⼤对象进⼊⽼年代的年龄，让对象在新⽣代多存活⼀段时间。 

\3. 空间分配担保失败 

使⽤复制算法的 Minor GC 需要⽼年代的内存空间作担保，如果担保失败会执⾏⼀次 Full GC。具体内 

容请参考上⾯的第 5 ⼩节。 

\4. JDK 1.7 及以前的永久代空间不⾜ 

在 JDK 1.7 及以前，HotSpot 虚拟机中的⽅法区是⽤永久代实现的，永久代中存放的为⼀些 Class 的信 

息、常量、静态变量等数据。 

当系统中要加载的类、反射的类和调⽤的⽅法较多时，永久代可能会被占满，在未配置为采⽤ CMS GC 

的情况下也会执⾏ Full GC。如果经过 Full GC 仍然回收不了，那么虚拟机会抛出 

java.lang.OutOfMemoryError。 

为避免以上原因引起的 Full GC，可采⽤的⽅法为增⼤永久代空间或转为使⽤ CMS GC。 

\5. Concurrent Mode Failure 

执⾏ CMS GC 的过程中同时有对象要放⼊⽼年代，⽽此时⽼年代空间不⾜（可能是 GC 过程中浮动垃 

圾过多导致暂时性的空间不⾜），便会报 Concurrent Mode Failure 错误，并触发 Full GC。 

四、类加载机制 

类是在运⾏期间第⼀次使⽤时动态加载的，⽽不是⼀次性加载所有类。因为如果⼀次性加载，那么会占 

⽤很多的内存。 

类的⽣命周期 

包括以下 7 个阶段： 

加载（**Loading**）  

验证（**Verification**）  

准备（**Preparation**）  

解析（**Resolution**） 初始化（**Initialization**）  

使⽤（Using） 

卸载（Unloading） 

类加载过程 

包含了加载、验证、准备、解析和初始化这 5 个阶段。 

\1. 加载 

加载是类加载的⼀个阶段，注意不要混淆。 

加载过程完成以下三件事： 

通过类的完全限定名称获取定义该类的⼆进制字节流。 

将该字节流表示的静态存储结构转换为⽅法区的运⾏时存储结构。 

在内存中⽣成⼀个代表该类的 Class 对象，作为⽅法区中该类各种数据的访问⼊⼝。 

其中⼆进制字节流可以从以下⽅式中获取： 

从 ZIP 包读取，成为 JAR、EAR、WAR 格式的基础。 

从⽹络中获取，最典型的应⽤是 Applet。 

运⾏时计算⽣成，例如动态代理技术，在 java.lang.reflect.Proxy 使⽤ 

ProxyGenerator.generateProxyClass 的代理类的⼆进制字节流。 

由其他⽂件⽣成，例如由 JSP ⽂件⽣成对应的 Class 类。 

\2. 验证 

确保 Class ⽂件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机⾃身的安全。 

\3. 准备 

类变量是被 static 修饰的变量，准备阶段为类变量分配内存并设置初始值，使⽤的是⽅法区的内存。 

实例变量不会在这阶段分配内存，它会在对象实例化时随着对象⼀起被分配在堆中。应该注意到，实例 

化不是类加载的⼀个过程，类加载发⽣在所有实例化操作之前，并且类加载只进⾏⼀次，实例化可以进 

⾏多次。 

初始值⼀般为 0 值，例如下⾯的类变量 value 被初始化为 0 ⽽不是 123。 

如果类变量是常量，那么它将初始化为表达式所定义的值⽽不是 0。例如下⾯的常量 value 被初始化为 

123 ⽽不是 0。 

public static int value = 123;4. 解析 

将常量池的符号引⽤替换为直接引⽤的过程。 

其中解析过程在某些情况下可以在初始化阶段之后再开始，这是为了⽀持 Java 的动态绑定。 

\5. 初始化 

初始化阶段才真正开始执⾏类中定义的 Java 程序代码。初始化阶段是虚拟机执⾏类构造器 <clinit>() ⽅ 

法的过程。在准备阶段，类变量已经赋过⼀次系统要求的初始值，⽽在初始化阶段，根据程序员通过程 

序制定的主观计划去初始化类变量和其它资源。 

<clinit>() 是由编译器⾃动收集类中所有类变量的赋值动作和静态语句块中的语句合并产⽣的，编译器收 

集的顺序由语句在源⽂件中出现的顺序决定。特别注意的是，静态语句块只能访问到定义在它之前的类 

变量，定义在它之后的类变量只能赋值，不能访问。例如以下代码： 

由于⽗类的 <clinit>() ⽅法先执⾏，也就意味着⽗类中定义的静态语句块的执⾏要优先于⼦类。例如以下 

代码： 

public static final int value = 123; 

public class Test { 

 static { 

 i = 0; // 给变量赋值可以正常编译通过 

 System.out.print(i); // 这句编译器会提示“⾮法向前引⽤” 

 } 

 static int i = 1; 

} 

static class Parent { 

 public static int A = 1; 

 static { 

 A = 2; 

 } 

} 

static class Sub extends Parent { 

 public static int B = A; 

} 

public static void main(String[] args) { 

 System.out.println(Sub.B); // 2 

}接⼝中不可以使⽤静态语句块，但仍然有类变量初始化的赋值操作，因此接⼝与类⼀样都会⽣成 

<clinit>() ⽅法。但接⼝与类不同的是，执⾏接⼝的 <clinit>() ⽅法不需要先执⾏⽗接⼝的 <clinit>() ⽅ 

法。只有当⽗接⼝中定义的变量使⽤时，⽗接⼝才会初始化。另外，接⼝的实现类在初始化时也⼀样不 

会执⾏接⼝的 <clinit>() ⽅法。 

虚拟机会保证⼀个类的 <clinit>() ⽅法在多线程环境下被正确的加锁和同步，如果多个线程同时初始化⼀ 

个类，只会有⼀个线程执⾏这个类的 <clinit>() ⽅法，其它线程都会阻塞等待，直到活动线程执⾏ 

<clinit>() ⽅法完毕。如果在⼀个类的 <clinit>() ⽅法中有耗时的操作，就可能造成多个线程阻塞，在实 

际过程中此种阻塞很隐蔽。 

类初始化时机 

\1. 主动引⽤ 

虚拟机规范中并没有强制约束何时进⾏加载，但是规范严格规定了有且只有下列五种情况必须对类进⾏ 

初始化（加载、验证、准备都会随之发⽣）： 

遇到 new、getstatic、putstatic、invokestatic 这四条字节码指令时，如果类没有进⾏过初始化，则 

必须先触发其初始化。最常⻅的⽣成这 4 条指令的场景是：使⽤ new 关键字实例化对象的时候； 

读取或设置⼀个类的静态字段（被 final 修饰、已在编译期把结果放⼊常量池的静态字段除外）的时 

候；以及调⽤⼀个类的静态⽅法的时候。 

使⽤ java.lang.reflect 包的⽅法对类进⾏反射调⽤的时候，如果类没有进⾏初始化，则需要先触发 

其初始化。 

当初始化⼀个类的时候，如果发现其⽗类还没有进⾏过初始化，则需要先触发其⽗类的初始化。 

当虚拟机启动时，⽤户需要指定⼀个要执⾏的主类（包含 main() ⽅法的那个类），虚拟机会先初始 

化这个主类； 

当使⽤ JDK 1.7 的动态语⾔⽀持时，如果⼀个 java.lang.invoke.MethodHandle 实例最后的解析结 

果为 REF_getStatic, REF_putStatic, REF_invokeStatic 的⽅法句柄，并且这个⽅法句柄所对应的类 

没有进⾏过初始化，则需要先触发其初始化； 

\2. 被动引⽤ 

以上 5 种场景中的⾏为称为对⼀个类进⾏主动引⽤。除此之外，所有引⽤类的⽅式都不会触发初始化， 

称为被动引⽤。被动引⽤的常⻅例⼦包括： 

通过⼦类引⽤⽗类的静态字段，不会导致⼦类初始化。 

通过数组定义来引⽤类，不会触发此类的初始化。该过程会对数组类进⾏初始化，数组类是⼀个由 

虚拟机⾃动⽣成的、直接继承⾃ Object 的⼦类，其中包含了数组的属性和⽅法。 

System.out.println(SubClass.value); // value 字段在 SuperClass 中定义 

SuperClass[] sca = new SuperClass[10];常量在编译阶段会存⼊调⽤类的常量池中，本质上并没有直接引⽤到定义常量的类，因此不会触发 

定义常量的类的初始化。 

类与类加载器 

两个类相等，需要类本身相等，并且使⽤同⼀个类加载器进⾏加载。这是因为每⼀个类加载器都拥有⼀ 

个独⽴的类名称空间。 

这⾥的相等，包括类的 Class 对象的 equals() ⽅法、isAssignableFrom() ⽅法、isInstance() ⽅法的返回 

结果为 true，也包括使⽤ instanceof 关键字做对象所属关系判定结果为 true。 

类加载器分类 

从 Java 虚拟机的⻆度来讲，只存在以下两种不同的类加载器： 

启动类加载器（Bootstrap ClassLoader），使⽤ C++ 实现，是虚拟机⾃身的⼀部分； 

所有其它类的加载器，使⽤ Java 实现，独⽴于虚拟机，继承⾃抽象类 java.lang.ClassLoader。 

从 Java 开发⼈员的⻆度看，类加载器可以划分得更细致⼀些： 

启动类加载器（Bootstrap ClassLoader）此类加载器负责将存放在 <JRE_HOME>\lib ⽬录中的， 

或者被 -Xbootclasspath 参数所指定的路径中的，并且是虚拟机识别的（仅按照⽂件名识别，如 

rt.jar，名字不符合的类库即使放在 lib ⽬录中也不会被加载）类库加载到虚拟机内存中。启动类加 

载器⽆法被 Java 程序直接引⽤，⽤户在编写⾃定义类加载器时，如果需要把加载请求委派给启动 

类加载器，直接使⽤ null 代替即可。 

扩展类加载器（Extension ClassLoader）这个类加载器是由 

ExtClassLoader（sun.misc.Launcher$ExtClassLoader）实现的。它负责将 <JAVA_HOME>/lib/ext 

或者被 java.ext.dir 系统变量所指定路径中的所有类库加载到内存中，开发者可以直接使⽤扩展类加 

载器。 

应⽤程序类加载器（Application ClassLoader）这个类加载器是由 

AppClassLoader（sun.misc.Launcher$AppClassLoader）实现的。由于这个类加载器是 

ClassLoader 中的 getSystemClassLoader() ⽅法的返回值，因此⼀般称为系统类加载器。它负责加 

载⽤户类路径（ClassPath）上所指定的类库，开发者可以直接使⽤这个类加载器，如果应⽤程序 

中没有⾃定义过⾃⼰的类加载器，⼀般情况下这个就是程序中默认的类加载器。 

双亲委派模型 

应⽤程序是由三种类加载器互相配合从⽽实现类加载，除此之外还可以加⼊⾃⼰定义的类加载器。 

下图展示了类加载器之间的层次关系，称为双亲委派模型（Parents Delegation Model）。该模型要求 

除了顶层的启动类加载器外，其它的类加载器都要有⾃⼰的⽗类加载器。这⾥的⽗⼦关系⼀般通过组合 

关系（Composition）来实现，⽽不是继承关系（Inheritance）。 

System.out.println(ConstClass.HELLOWORLD);1. ⼯作过程 

⼀个类加载器⾸先将类加载请求转发到⽗类加载器，只有当⽗类加载器⽆法完成时才尝试⾃⼰加载。 

\2. 好处 

使得 Java 类随着它的类加载器⼀起具有⼀种带有优先级的层次关系，从⽽使得基础类得到统⼀。 

例如 java.lang.Object 存放在 rt.jar 中，如果编写另外⼀个 java.lang.Object 并放到 ClassPath 中，程序 

可以编译通过。由于双亲委派模型的存在，所以在 rt.jar 中的 Object ⽐在 ClassPath 中的 Object 优先 

级更⾼，这是因为 rt.jar 中的 Object 使⽤的是启动类加载器，⽽ ClassPath 中的 Object 使⽤的是应⽤ 

程序类加载器。rt.jar 中的 Object 优先级更⾼，那么程序中所有的 Object 都是这个 Object。 

\3. 实现 

以下是抽象类 java.lang.ClassLoader 的代码⽚段，其中的 loadClass() ⽅法运⾏过程如下：先检查类是 

否已经加载过，如果没有则让⽗类加载器去加载。当⽗类加载器加载失败时抛出 

ClassNotFoundException，此时尝试⾃⼰去加载。 

public abstract class ClassLoader { 

 // The parent class loader for delegation 

 private final ClassLoader parent;⾃定义类加载器实现 

 public Class<?> loadClass(String name) throws ClassNotFoundException { 

 return loadClass(name, false); 

 } 

 protected Class<?> loadClass(String name, boolean resolve) throws 

ClassNotFoundException { 

 synchronized (getClassLoadingLock(name)) { 

 // First, check if the class has already been loaded 

 Class<?> c = findLoadedClass(name); 

 if (c == null) { 

 try { 

 if (parent != null) { 

 c = parent.loadClass(name, false); 

 } else { 

 c = findBootstrapClassOrNull(name); 

 } 

 } catch (ClassNotFoundException e) { 

 // ClassNotFoundException thrown if class not found 

 // from the non-null parent class loader 

 } 

 if (c == null) { 

 // If still not found, then invoke findClass in order 

 // to find the class. 

 c = findClass(name); 

 } 

 } 

 if (resolve) { 

 resolveClass(c); 

 } 

 return c; 

 } 

 } 

 protected Class<?> findClass(String name) throws ClassNotFoundException 

{ 

 throw new ClassNotFoundException(name); 

 } 

}以下代码中的 FileSystemClassLoader 是⾃定义类加载器，继承⾃ java.lang.ClassLoader，⽤于加载⽂ 

件系统上的类。它⾸先根据类的全名在⽂件系统上查找类的字节代码⽂件（.class ⽂件），然后读取该 

⽂件内容，最后通过 defineClass() ⽅法来把这些字节代码转换成 java.lang.Class 类的实例。 

java.lang.ClassLoader 的 loadClass() 实现了双亲委派模型的逻辑，⾃定义类加载器⼀般不去重写它， 

但是需要重写 findClass() ⽅法。 

public class FileSystemClassLoader extends ClassLoader { 

 private String rootDir; 

 public FileSystemClassLoader(String rootDir) { 

 this.rootDir = rootDir; 

 } 

 protected Class<?> findClass(String name) throws ClassNotFoundException 

{ 

 byte[] classData = getClassData(name); 

 if (classData == null) { 

 throw new ClassNotFoundException(); 

 } else { 

 return defineClass(name, classData, 0, classData.length); 

 } 

 } 

 private byte[] getClassData(String className) { 

 String path = classNameToPath(className); 

 try { 

 InputStream ins = new FileInputStream(path); 

 ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

 int bufferSize = 4096; 

 byte[] buffer = new byte[bufferSize]; 

 int bytesNumRead; 

 while ((bytesNumRead = ins.read(buffer)) != -1) { 

 baos.write(buffer, 0, bytesNumRead); 

 } 

 return baos.toByteArray(); 

 } catch (IOException e) { 

 e.printStackTrace(); 

 } 

 return null; 

 } 

 private String classNameToPath(String className) {参考资料 

周志明. 深⼊理解 Java 虚拟机 [M]. 机械⼯业出版社, 2011. 

Chapter 2. The Structure of the Java Virtual Machine 

Jvm memory 

Getting Started with the G1 Garbage Collector 

JNI Part1: Java Native Interface Introduction and “Hello World” application 

Memory Architecture Of JVM(Runtime Data Areas) 

JVM Run-Time Data Areas 

Android on x86: Java Native Interface and the Android Native Development Kit 

深⼊理解 JVM(2)——GC 算法与内存分配策略 

深⼊理解 JVM(3)——7 种垃圾收集器 

JVM Internals 

深⼊探讨 Java 类加载器 

Guide to WeakHashMap in Java 

Tomcat example source code file (ConcurrentCache.java) 

Java IO 

Java IO 

⼀、概览 

⼆、磁盘操作 

三、字节操作 

实现⽂件复制 

装饰者模式 

四、字符操作 

编码与解码 

String 的编码⽅式 

Reader 与 Writer 

实现逐⾏输出⽂本⽂件的内容 

五、对象操作 

序列化 

Serializable 

transient 

 return rootDir + File.separatorChar 

 \+ className.replace('.', File.separatorChar) + ".class"; 

 } 

}六、⽹络操作 

InetAddress 

URL 

Sockets 

Datagram 

七、NIO 

流与块 

通道与缓冲区 

缓冲区状态变量 

⽂件 NIO 实例 

选择器 

套接字 NIO 实例 

内存映射⽂件 

对⽐ 

⼋、参考资料 

⼀、概览 

Java 的 I/O ⼤概可以分成以下⼏类： 

磁盘操作：File 

字节操作：InputStream 和 OutputStream 

字符操作：Reader 和 Writer 

对象操作：Serializable 

⽹络操作：Socket 

新的输⼊/输出：NIO 

⼆、磁盘操作 

File 类可以⽤于表示⽂件和⽬录的信息，但是它不表示⽂件的内容。 

递归地列出⼀个⽬录下所有⽂件：从 Java7 开始，可以使⽤ Paths 和 Files 代替 File。 

三、字节操作 

实现⽂件复制 

装饰者模式 

Java I/O 使⽤了装饰者模式来实现。以 InputStream 为例， 

InputStream 是抽象组件； 

FileInputStream 是 InputStream 的⼦类，属于具体组件，提供了字节流的输⼊操作； 

public static void listAllFiles(File dir) { 

 if (dir == null || !dir.exists()) { 

 return; 

 } 

 if (dir.isFile()) { 

 System.out.println(dir.getName()); 

 return; 

 } 

 for (File file : dir.listFiles()) { 

 listAllFiles(file); 

 } 

} 

public static void copyFile(String src, String dist) throws IOException { 

 FileInputStream in = new FileInputStream(src); 

 FileOutputStream out = new FileOutputStream(dist); 

 byte[] buffer = new byte[20 * 1024]; 

 int cnt; 

 // read() 最多读取 buffer.length 个字节 

 // 返回的是实际读取的个数 

 // 返回 -1 的时候表示读到 eof，即⽂件尾 

 while ((cnt = in.read(buffer, 0, buffer.length)) != -1) { 

 out.write(buffer, 0, cnt); 

 } 

 in.close(); 

 out.close(); 

}FilterInputStream 属于抽象装饰者，装饰者⽤于装饰组件，为组件提供额外的功能。例如 

BufferedInputStream 为 FileInputStream 提供缓存的功能。 

实例化⼀个具有缓存功能的字节流对象时，只需要在 FileInputStream 对象上再套⼀层 

BufferedInputStream 对象即可。 

DataInputStream 装饰者提供了对更多数据类型进⾏输⼊的操作，⽐如 int、double 等基本类型。 

四、字符操作 

编码与解码 

编码就是把字符转换为字节，⽽解码是把字节重新组合成字符。 

如果编码和解码过程使⽤不同的编码⽅式那么就出现了乱码。 

GBK 编码中，中⽂字符占 2 个字节，英⽂字符占 1 个字节； 

UTF-8 编码中，中⽂字符占 3 个字节，英⽂字符占 1 个字节； 

UTF-16be 编码中，中⽂字符和英⽂字符都占 2 个字节。 

UTF-16be 中的 be 指的是 Big Endian，也就是⼤端。相应地也有 UTF-16le，le 指的是 Little Endian， 

也就是⼩端。 

Java 的内存编码使⽤双字节编码 UTF-16be，这不是指 Java 只⽀持这⼀种编码⽅式，⽽是说 char 这种 

类型使⽤ UTF-16be 进⾏编码。char 类型占 16 位，也就是两个字节，Java 使⽤这种双字节编码是为了 

让⼀个中⽂或者⼀个英⽂都能使⽤⼀个 char 来存储。 

**String** 的编码⽅式 

FileInputStream fileInputStream = new FileInputStream(filePath); 

BufferedInputStream bufferedInputStream = new 

BufferedInputStream(fileInputStream);String 可以看成⼀个字符序列，可以指定⼀个编码⽅式将它编码为字节序列，也可以指定⼀个编码⽅式 

将⼀个字节序列解码为 String。 

在调⽤⽆参数 getBytes() ⽅法时，默认的编码⽅式不是 UTF-16be。双字节编码的好处是可以使⽤⼀个 

char 存储中⽂和英⽂，⽽将 String 转为 bytes[] 字节数组就不再需要这个好处，因此也就不再需要双字 

节编码。getBytes() 的默认编码⽅式与平台有关，⼀般为 UTF-8。 

**Reader** 与 **Writer** 

不管是磁盘还是⽹络传输，最⼩的存储单元都是字节，⽽不是字符。但是在程序中操作的通常是字符形 

式的数据，因此需要提供对字符进⾏操作的⽅法。 

InputStreamReader 实现从字节流解码成字符流； 

OutputStreamWriter 实现字符流编码成为字节流。 

实现逐⾏输出⽂本⽂件的内容 

五、对象操作 

String str1 = "中⽂"; 

byte[] bytes = str1.getBytes("UTF-8"); 

String str2 = new String(bytes, "UTF-8"); 

System.out.println(str2); 

byte[] bytes = str1.getBytes(); 

public static void readFileContent(String filePath) throws IOException { 

 FileReader fileReader = new FileReader(filePath); 

 BufferedReader bufferedReader = new BufferedReader(fileReader); 

 String line; 

 while ((line = bufferedReader.readLine()) != null) { 

 System.out.println(line); 

 } 

 // 装饰者模式使得 BufferedReader 组合了⼀个 Reader 对象 

 // 在调⽤ BufferedReader 的 close() ⽅法时会去调⽤ Reader 的 close() ⽅法 

 // 因此只要⼀个 close() 调⽤即可 

 bufferedReader.close(); 

}序列化 

序列化就是将⼀个对象转换成字节序列，⽅便存储和传输。 

序列化：ObjectOutputStream.writeObject() 

反序列化：ObjectInputStream.readObject() 

不会对静态变量进⾏序列化，因为序列化只是保存对象的状态，静态变量属于类的状态。 

**Serializable** 

序列化的类需要实现 Serializable 接⼝，它只是⼀个标准，没有任何⽅法需要实现，但是如果不去实现 

它的话⽽进⾏序列化，会抛出异常。 

public static void main(String[] args) throws IOException, 

ClassNotFoundException { 

 A a1 = new A(123, "abc"); 

 String objectFile = "file/a1"; 

 ObjectOutputStream objectOutputStream = new ObjectOutputStream(new 

FileOutputStream(objectFile)); 

 objectOutputStream.writeObject(a1); 

 objectOutputStream.close(); 

 ObjectInputStream objectInputStream = new ObjectInputStream(new 

FileInputStream(objectFile)); 

 A a2 = (A) objectInputStream.readObject(); 

 objectInputStream.close(); 

 System.out.println(a2); 

} 

private static class A implements Serializable { 

 private int x; 

 private String y; 

 A(int x, String y) { 

 this.x = x; 

 this.y = y; 

 } 

 @Override 

 public String toString() { 

 return "x = " + x + " " + "y = " + y;**transient** 

transient 关键字可以使⼀些属性不会被序列化。 

ArrayList 中存储数据的数组 elementData 是⽤ transient 修饰的，因为这个数组是动态扩展的，并不是 

所有的空间都被使⽤，因此就不需要所有的内容都被序列化。通过重写序列化和反序列化⽅法，使得可 

以只序列化数组中有内容的那部分数据。 

六、⽹络操作 

Java 中的⽹络⽀持： 

InetAddress：⽤于表示⽹络上的硬件资源，即 IP 地址； 

URL：统⼀资源定位符； 

Sockets：使⽤ TCP 协议实现⽹络通信； 

Datagram：使⽤ UDP 协议实现⽹络通信。 

**InetAddress** 

没有公有的构造函数，只能通过静态⽅法来创建实例。 

**URL** 

可以直接从 URL 中读取字节流数据。 

 } 

} 

private transient Object[] elementData; 

InetAddress.getByName(String host); 

InetAddress.getByAddress(byte[] address); 

public static void main(String[] args) throws IOException { 

 URL url = new URL("http://www.baidu.com"); 

 /* 字节流 */ 

 InputStream is = url.openStream(); 

 /* 字符流 */ 

 InputStreamReader isr = new InputStreamReader(is, "utf-8");**Sockets** 

ServerSocket：服务器端类 

Socket：客户端类 

服务器和客户端通过 InputStream 和 OutputStream 进⾏输⼊输出。 

**Datagram** 

DatagramSocket：通信类 

DatagramPacket：数据包类 

七、**NIO** 

新的输⼊/输出 (NIO) 库是在 JDK 1.4 中引⼊的，弥补了原来的 I/O 的不⾜，提供了⾼速的、⾯向块的 

I/O。 

 /* 提供缓存功能 */ 

 BufferedReader br = new BufferedReader(isr); 

 String line; 

 while ((line = br.readLine()) != null) { 

 System.out.println(line); 

 } 

 br.close(); 

}流与块 

I/O 与 NIO 最重要的区别是数据打包和传输的⽅式，I/O 以流的⽅式处理数据，⽽ NIO 以块的⽅式处理 

数据。 

⾯向流的 I/O ⼀次处理⼀个字节数据：⼀个输⼊流产⽣⼀个字节数据，⼀个输出流消费⼀个字节数据。 

为流式数据创建过滤器⾮常容易，链接⼏个过滤器，以便每个过滤器只负责复杂处理机制的⼀部分。不 

利的⼀⾯是，⾯向流的 I/O 通常相当慢。 

⾯向块的 I/O ⼀次处理⼀个数据块，按块处理数据⽐按流处理数据要快得多。但是⾯向块的 I/O 缺少⼀ 

些⾯向流的 I/O 所具有的优雅性和简单性。 

I/O 包和 NIO 已经很好地集成了，java.io.* 已经以 NIO 为基础重新实现了，所以现在它可以利⽤ NIO 的 

⼀些特性。例如，java.io.* 包中的⼀些类包含以块的形式读写数据的⽅法，这使得即使在⾯向流的系统 

中，处理速度也会更快。 

通道与缓冲区 

\1. 通道 

通道 Channel 是对原 I/O 包中的流的模拟，可以通过它读取和写⼊数据。 

通道与流的不同之处在于，流只能在⼀个⽅向上移动(⼀个流必须是 InputStream 或者 OutputStream 的 

⼦类)，⽽通道是双向的，可以⽤于读、写或者同时⽤于读写。 

通道包括以下类型： 

FileChannel：从⽂件中读写数据； 

DatagramChannel：通过 UDP 读写⽹络中数据； 

SocketChannel：通过 TCP 读写⽹络中数据； 

ServerSocketChannel：可以监听新进来的 TCP 连接，对每⼀个新进来的连接都会创建⼀个 

SocketChannel。 

\2. 缓冲区 

发送给⼀个通道的所有数据都必须⾸先放到缓冲区中，同样地，从通道中读取的任何数据都要先读到缓 

冲区中。也就是说，不会直接对通道进⾏读写数据，⽽是要先经过缓冲区。 

缓冲区实质上是⼀个数组，但它不仅仅是⼀个数组。缓冲区提供了对数据的结构化访问，⽽且还可以跟 

踪系统的读/写进程。 

缓冲区包括以下类型： 

ByteBuffer 

CharBuffer 

ShortBufferIntBuffer 

LongBuffer 

FloatBuffer 

DoubleBuffer 

缓冲区状态变量 

capacity：最⼤容量； 

position：当前已经读写的字节数； 

limit：还可以读写的字节数。 

状态变量的改变过程举例： 

① 新建⼀个⼤⼩为 8 个字节的缓冲区，此时 position 为 0，⽽ limit = capacity = 8。capacity 变量不会 

改变，下⾯的讨论会忽略它。 

② 从输⼊通道中读取 5 个字节数据写⼊缓冲区中，此时 position 为 5，limit 保持不变。 

③ 在将缓冲区的数据写到输出通道之前，需要先调⽤ flip() ⽅法，这个⽅法将 limit 设置为当前 

position，并将 position 设置为 0。④ 从缓冲区中取 4 个字节到输出缓冲中，此时 position 设为 4。 

⑤ 最后需要调⽤ clear() ⽅法来清空缓冲区，此时 position 和 limit 都被设置为最初位置。 

⽂件 **NIO** 实例 

以下展示了使⽤ NIO 快速复制⽂件的实例： 

public static void fastCopy(String src, String dist) throws IOException { 

 /* 获得源⽂件的输⼊字节流 */ 

 FileInputStream fin = new FileInputStream(src); 

 /* 获取输⼊字节流的⽂件通道 */ 

 FileChannel fcin = fin.getChannel(); 

 /* 获取⽬标⽂件的输出字节流 */ 

 FileOutputStream fout = new FileOutputStream(dist); 

 /* 获取输出字节流的⽂件通道 */选择器 

NIO 常常被叫做⾮阻塞 IO，主要是因为 NIO 在⽹络通信中的⾮阻塞特性被⼴泛使⽤。 

NIO 实现了 IO 多路复⽤中的 Reactor 模型，⼀个线程 Thread 使⽤⼀个选择器 Selector 通过轮询的⽅ 

式去监听多个通道 Channel 上的事件，从⽽让⼀个线程就可以处理多个事件。 

通过配置监听的通道 Channel 为⾮阻塞，那么当 Channel 上的 IO 事件还未到达时，就不会进⼊阻塞状 

态⼀直等待，⽽是继续轮询其它 Channel，找到 IO 事件已经到达的 Channel 执⾏。 

因为创建和切换线程的开销很⼤，因此使⽤⼀个线程来处理多个事件⽽不是⼀个线程处理⼀个事件，对 

于 IO 密集型的应⽤具有很好地性能。 

应该注意的是，只有套接字 Channel 才能配置为⾮阻塞，⽽ FileChannel 不能，为 FileChannel 配置⾮ 

阻塞也没有意义。 

 FileChannel fcout = fout.getChannel(); 

 /* 为缓冲区分配 1024 个字节 */ 

 ByteBuffer buffer = ByteBuffer.allocateDirect(1024); 

 while (true) { 

 /* 从输⼊通道中读取数据到缓冲区中 */ 

 int r = fcin.read(buffer); 

 /* read() 返回 -1 表示 EOF */ 

 if (r == -1) { 

 break; 

 } 

 /* 切换读写 */ 

 buffer.flip(); 

 /* 把缓冲区的内容写⼊输出⽂件中 */ 

 fcout.write(buffer); 

 /* 清空缓冲区 */ 

 buffer.clear(); 

 } 

}1. 创建选择器 

\2. 将通道注册到选择器上 

通道必须配置为⾮阻塞模式，否则使⽤选择器就没有任何意义了，因为如果通道在某个事件上被阻塞， 

那么服务器就不能响应其它事件，必须等待这个事件处理完毕才能去处理其它事件，显然这和选择器的 

作⽤背道⽽驰。 

在将通道注册到选择器上时，还需要指定要注册的具体事件，主要有以下⼏类： 

SelectionKey.OP_CONNECT 

SelectionKey.OP_ACCEPT 

SelectionKey.OP_READ 

SelectionKey.OP_WRITE 

它们在 SelectionKey 的定义如下： 

可以看出每个事件可以被当成⼀个位域，从⽽组成事件集整数。例如： 

Selector selector = Selector.open(); 

ServerSocketChannel ssChannel = ServerSocketChannel.open(); 

ssChannel.configureBlocking(false); 

ssChannel.register(selector, SelectionKey.OP_ACCEPT); 

public static final int OP_READ = 1 << 0; 

public static final int OP_WRITE = 1 << 2; 

public static final int OP_CONNECT = 1 << 3; 

public static final int OP_ACCEPT = 1 << 4;3. 监听事件 

使⽤ select() 来监听到达的事件，它会⼀直阻塞直到有⾄少⼀个事件到达。 

\4. 获取到达的事件 

\5. 事件循环 

因为⼀次 select() 调⽤不能处理完所有的事件，并且服务器端有可能需要⼀直监听事件，因此服务器端 

处理事件的代码⼀般会放在⼀个死循环内。 

int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE; 

int num = selector.select(); 

Set<SelectionKey> keys = selector.selectedKeys(); 

Iterator<SelectionKey> keyIterator = keys.iterator(); 

while (keyIterator.hasNext()) { 

 SelectionKey key = keyIterator.next(); 

 if (key.isAcceptable()) { 

 // ... 

 } else if (key.isReadable()) { 

 // ... 

 } 

 keyIterator.remove(); 

} 

while (true) { 

 int num = selector.select(); 

 Set<SelectionKey> keys = selector.selectedKeys(); 

 Iterator<SelectionKey> keyIterator = keys.iterator(); 

 while (keyIterator.hasNext()) { 

 SelectionKey key = keyIterator.next(); 

 if (key.isAcceptable()) { 

 // ... 

 } else if (key.isReadable()) { 

 // ... 

 } 

 keyIterator.remove(); 

 } 

}套接字 **NIO** 实例 

public class NIOServer { 

 public static void main(String[] args) throws IOException { 

 Selector selector = Selector.open(); 

 ServerSocketChannel ssChannel = ServerSocketChannel.open(); 

 ssChannel.configureBlocking(false); 

 ssChannel.register(selector, SelectionKey.OP_ACCEPT); 

 ServerSocket serverSocket = ssChannel.socket(); 

 InetSocketAddress address = new InetSocketAddress("127.0.0.1", 

8888); 

 serverSocket.bind(address); 

 while (true) { 

 selector.select(); 

 Set<SelectionKey> keys = selector.selectedKeys(); 

 Iterator<SelectionKey> keyIterator = keys.iterator(); 

 while (keyIterator.hasNext()) { 

 SelectionKey key = keyIterator.next(); 

 if (key.isAcceptable()) { 

 ServerSocketChannel ssChannel1 = (ServerSocketChannel) 

key.channel(); 

 // 服务器会为每个新连接创建⼀个 SocketChannel 

 SocketChannel sChannel = ssChannel1.accept(); 

 sChannel.configureBlocking(false); 

 // 这个新连接主要⽤于从客户端读取数据 

 sChannel.register(selector, SelectionKey.OP_READ); 

 } else if (key.isReadable()) { 

 SocketChannel sChannel = (SocketChannel) key.channel(); 

  

System.out.println(readDataFromSocketChannel(sChannel)); sChannel.close(); 

 } 

 keyIterator.remove(); 

 } 

 } 

 } 

 private static String readDataFromSocketChannel(SocketChannel sChannel) 

throws IOException { 

 ByteBuffer buffer = ByteBuffer.allocate(1024); 

 StringBuilder data = new StringBuilder(); 

 while (true) { 

 buffer.clear(); 

 int n = sChannel.read(buffer); 

 if (n == -1) { 

 break; 

 } 

 buffer.flip(); 

 int limit = buffer.limit(); 

 char[] dst = new char[limit]; 

 for (int i = 0; i < limit; i++) { 

 dst[i] = (char) buffer.get(i); 

 } 

 data.append(dst); 

 buffer.clear(); 

 } 

 return data.toString(); 

 } 

}内存映射⽂件 

内存映射⽂件 I/O 是⼀种读和写⽂件数据的⽅法，它可以⽐常规的基于流或者基于通道的 I/O 快得多。 

向内存映射⽂件写⼊可能是危险的，只是改变数组的单个元素这样的简单操作，就可能会直接修改磁盘 

上的⽂件。修改数据与将数据保存到磁盘是没有分开的。 

下⾯代码⾏将⽂件的前 1024 个字节映射到内存中，map() ⽅法返回⼀个 MappedByteBuffer，它是 

ByteBuffer 的⼦类。因此，可以像使⽤其他任何 ByteBuffer ⼀样使⽤新映射的缓冲区，操作系统会在需 

要时负责执⾏映射。 

对⽐ 

NIO 与普通 I/O 的区别主要有以下两点： 

NIO 是⾮阻塞的； 

NIO ⾯向块，I/O ⾯向流。 

⼋、参考资料 

Eckel B, 埃克尔, 昊鹏, 等. Java 编程思想 [M]. 机械⼯业出版社, 2002. 

IBM: NIO ⼊⻔ 

Java NIO Tutorial 

Java NIO 浅析 

IBM: 深⼊分析 Java I/O 的⼯作机制 

IBM: 深⼊分析 Java 中的中⽂编码问题 

IBM: Java 序列化的⾼级认识 

NIO 与传统 IO 的区别 

Decorator Design Pattern 

Socket Multicast 

public class NIOClient { 

 public static void main(String[] args) throws IOException { 

 Socket socket = new Socket("127.0.0.1", 8888); 

 OutputStream out = socket.getOutputStream(); 

 String s = "hello world"; 

 out.write(s.getBytes()); 

 out.close(); 

 } 

} 

MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1024);one more thing 

如果你觉得这份资料不错的话，欢迎关注「沉默王⼆」公众号，回复「Java」有更多惊喜哦。