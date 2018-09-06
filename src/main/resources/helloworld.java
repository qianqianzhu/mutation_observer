package org.qzhu.example;


public class HelloWorld {
    int member = 0;
    public HelloWorld(){
        if(member==0){
            System.out.println("You are the first!");
        }
    }

   public static void main(String[] args) {
       for(int i=0; i<10;i++){
           if(i==3){
               if(i==4) {
                   System.out.println("Hello, World");
               }else if(i==5){
                   System.out.println("Hello, World");
               }else{
                   System.out.println("Hello, World");
               }
           }
       }
   }

    public static void doStatementExample(int i){
        int count = 1;
        do {
            System.out.println("Count is: " + count);
            count++;
        } while (count < 11);
    }

    int indexOf(TestString str, int fromIndex) {
        char[] v1 = value, v2 = str.value;
        int max = offset + (count - str.count);
        int start = offset + ((fromIndex < 0) ? 0 : fromIndex);
        i:
        for (int i = start; i <= max; i++) {
            int n = str.count, j = i, k = str.offset;
            while (n-- != 0) {
                if (v1[j++] != v2[k++])
                    continue i;
            }
            return i - offset;
        }
        return -1;
    }
}

