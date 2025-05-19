/*
import java.io.FileWriter;
import java.io.IOException;

public class Kefel {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Kefel <multiplier>");
            return;
        }

        int k = Integer.parseInt(args[0]);
        int[] shifts = new int[64];
        int count = 0;

        for (int i = 0; i < 64; i++) {
            if (((k >> i) & 1) == 1) {
                shifts[count++] = i;
            }
        }

        FileWriter writer = new FileWriter("kefel.s");

        writer.write(".section .text\n");
        writer.write(".globl kefel\n");
        writer.write("kefel:\n");

        
        writer.write("    movq %rdi, %rax\n");
        if (shifts[0] != 0) {
            writer.write("    shlq $" + shifts[0] + ", %rax\n");
        }

       
        for (int i = 1; i < count; i++) {
            writer.write("    movq %rdi, %rcx\n");
            if (shifts[i] != 0) {
                writer.write("    shlq $" + shifts[i] + ", %rcx\n");
            }
            writer.write("    addq %rcx, %rax\n");
        }

        writer.write("    ret\n");
        writer.close();
    }
}
*/

import java.io.FileWriter;
import java.io.IOException;

public class Kefel {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Kefel <multiplier>");
            return;
        }

        int k = Integer.parseInt(args[0]);
        StringBuilder sb = new StringBuilder();
        sb.append(".section .text\n.globl kefel\nkefel: ");

        if (k == 0) {
            sb.append("movq $0, %rax; ret\n");
        } else if ((k & (k - 1)) == 0) { // Power of 2
            int shift = Integer.numberOfTrailingZeros(k);
            sb.append("movq %rdi, %rax; shlq $" + shift + ", %rax; ret\n");
        } else {
            StringBuilder best = null;
            int minLines = Integer.MAX_VALUE;

            // Try k = 2^a - 2^b
            for (int a = 1; a < 64; a++) {
                for (int b = 0; b < a; b++) {
                    if ((1 << a) - (1 << b) == k) {
                        StringBuilder temp = new StringBuilder();
                        temp.append("movq %rdi, %rax; shlq $" + a + ", %rax; ");
                        temp.append("movq %rdi, %rcx; shlq $" + b + ", %rcx; subq %rcx, %rax; ret\n");
                        if (1 < minLines) {
                            best = temp;
                            minLines = 1;
                        }
                    }
                }
            }

            // Try k = 2^a + 2^b
            for (int a = 0; a < 64; a++) {
                for (int b = 0; b < 64; b++) {
                    if (a == b) continue;
                    if ((1 << a) + (1 << b) == k) {
                        StringBuilder temp = new StringBuilder();
                        temp.append("movq %rdi, %rax;");
                        if (a != 0) temp.append(" shlq $" + a + ", %rax;");
                        temp.append(" movq %rdi, %rcx;");
                        if (b != 0) temp.append(" shlq $" + b + ", %rcx;");
                        temp.append(" addq %rcx, %rax; ret\n");
                        if (1 < minLines) {
                            best = temp;
                            minLines = 1;
                        }
                    }
                }
            }

            if (best != null) {
                sb.append(best);
            } else {
                boolean first = true;
                for (int i = 63; i >= 0; i--) {
                    if (((k >> i) & 1) == 1) {
                        if (first) {
                            sb.append("movq %rdi, %rax;");
                            if (i != 0) sb.append(" shlq $" + i + ", %rax;");
                            first = false;
                        } else {
                            sb.append(" movq %rdi, %rcx;");
                            if (i != 0) sb.append(" shlq $" + i + ", %rcx;");
                            sb.append(" addq %rcx, %rax;");
                        }
                    }
                }
                sb.append(" ret\n");
            }
        }

        FileWriter writer = new FileWriter("kefel.s");
        writer.write(sb.toString());
        writer.close();
    }
}

