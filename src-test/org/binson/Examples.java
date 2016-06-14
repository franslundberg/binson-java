package org.binson;

import org.binson.lowlevel.JsonOutput;

public class Examples {
	public static void main(String[] args) {
		Binson ex1 = new Binson().put("cid", 4);
		System.out.println("Example 1:");
		System.out.println("  {cid=4;}");
		System.out.println("  " + hex(ex1.toBytes()));
		
		Binson ex2 = new Binson();
		System.out.println("Example 2, empty object:");
		System.out.println("  {}");
		System.out.println("  " + hex(ex2.toBytes()));
		
		Binson ex3 = new Binson().put("a", new Binson().put("b", 2));
		System.out.println("Example 3, nested object:");
		System.out.println("  {a={b=2;};");
		System.out.println("  " + hex(ex3.toBytes()));
		
		Binson ex4 = new Binson()
				.put("a", 1)
				.put("b", new Binson().put("c", 3))
				.put("d", 4);
		System.out.println("Example 4, object field between integer fields:");
		System.out.println("  {a=1; b={c=3;}; d=4}");
		System.out.println("  " + hex(ex4.toBytes()));
		
		Binson ex5 = new Binson()
				.put("a", new BinsonArray().add(1).add("hello"));
		System.out.println("Example 5, array");
		System.out.println("  {a=[1, \"hello\"];}");
		System.out.println("  " + hex(ex5.toBytes()));
		
		Binson ex6 = new Binson()
				.put("a", 1)
				.put("b", new BinsonArray().add(10).add(20))
				.put("c", 3);
		System.out.println("ex6, array");
		System.out.println("  {a=1; b=[10,20]; c=3}");
		System.out.println("  " + hex(ex6.toBytes()));

		Binson ex7 = new Binson()
				.put("a", 1)
				.put("b", new BinsonArray().add(10).add(new BinsonArray().add(100).add(101)).add(20))
				.put("c", 3);
		System.out.println("ex7, array");
		System.out.println("  {a=1; b=[10, [100, 101], 20]; c=3}");
		System.out.println("  " + hex(ex7.toBytes()));
		
		Binson ex8 = new Binson()
				.put("a", 1)
				.put("b", -1)
				.put("c", 250)
				.put("d", Integer.MAX_VALUE)
				.put("f", Long.MAX_VALUE);
		System.out.println("ex8, array");
		System.out.println("  {a=1; b=-1; c=250; d=Integer.MAX_VALUE, f=Long.MAX_VALUE");
		System.out.println("  " + hex(ex8.toBytes()));
		
		Binson ex9 = new Binson()
				.put("aaaa", 250);
		System.out.println("ex9, int value = 250");
		System.out.println("  {aaaa=250}");
		System.out.println("  " + hex(ex9.toBytes()));
		
		Binson ex10 = new Binson()
                .put("aaaa", "bbb");
        System.out.println("ex10, short string value");
        System.out.println("  {aaaa=\"bbb\"}");
        System.out.println("  " + hex(ex10.toBytes()));
        
        Binson ex11 = new Binson()
                .put("aa", new byte[]{5, 5, 5});
        System.out.println("ex11, short bytes value");
        System.out.println("  {aa=0x050505;}");
        System.out.println("  " + hex(ex11.toBytes()));
	}
	
	private static String hex(byte[] bytes) {
		return JsonOutput.bytesToHex("", bytes);
	}
}

/*

Output (2016-01-06):

Example 1:
  {cid=4;}
  401403636964100441
Example 2, empty object:
  {}
  4041
Example 3, nested object:
  {a={b=2;};
  401401614014016210024141
Example 4, object field between integer fields:
  {a=1; b={c=3;}; d=4}
  40140161100114016240140163100341140164100441
Example 5, array
  {a=[1, "hello"];}
  40140161421001140568656c6c6f4341
ex6, array
  {a=1; b=[10,20]; c=3}
  40140161100114016242100a101443140163100341
ex7, array
  {a=1; b=[10, [100, 101], 20]; c=3}
  40140161100114016242100a421064106543101443140163100341
ex8, array
  {a=1; b=-1; c=250; d=Integer.MAX_VALUE, f=Long.MAX_VALUE
  40140161100114016210ff14016311fa0014016412ffffff7f14016613ffffffffffffff7f41
ex9, int value = 250
  {aaaa=250}
  4014046161616111fa0041
ex10, short string value
  {aaaa="bbb"}
  40140461616161140362626241
ex11, short bytes value
  {aa=0x050505;}
  4014026161180305050541
*/