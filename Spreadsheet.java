import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;

/* ------------------------------------------------------------------------------

 * My assumption regarding Cycle relationship example A1 : b1 - 5 and B1: A1 - 5
 * Here the value of b1 requires A1 to be calculated, hence cyclic.
 * I have taken into consideration negative numbers too
 */

/*
 * The overall solution idea is explained below:
 * Input: A1:A2
 * 		  A2:45*
 *        ...
 * --------------------------------------------------
 * Evaluation of A1:
 * Create a queue sheet_value and insert A2.
 * ___________________________________
 * |A2   |
 * |_____|____________________________
 * "queue that stores the reference in A1 which in this case "A2"  
 * 
 * As A2 is a expression (4 5 *) is evaluvated inside a stack
 * __________________________________
 * |4 | 5 | 
 * |__|___|__________________________
 *   "evaluvation stack (cell_value) pushes the constants and evaluvate the postfix expression when it 
 *    encounters "*" and the result is pushed back into the stack"
 *    
 * _______________________________
 * |20 | 
 * |_ _|__________________________          
 * " stack after evaluvation"
 * 
 * The queue is empty the function returns the result( 20 in this case).
 * 
 */


public class Spreadsheet {

	/**
	 * Lookup is created to store the result of computation, hence we
	 * dont have to perform it again
	 */
	static HashMap<String, String> lookup = new HashMap<String, String>();

	public static void main(String[] args) {

		int row = 0, col = 0;

		BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
		String[][] mat = null;
		String buffer[] = null;

		/**
		 * Get the row and col into variables, check for the following
		 * conditions if the row or column is not specified then exit with a
		 * message. If the number of row specified is greater than 26 or less
		 * than zero, column number is less than zero exit with a message.
		 */
		try {
			buffer = f.readLine().split(" ");
			if (buffer.length < 2) {
				System.out.println("Row or Column not defined");
				System.exit(1);
			}
			col = Integer.parseInt(buffer[0]);
			row = Integer.parseInt(buffer[1]);
			if ((row >= 1 || row <= 26) && (col >= 1)) {
				mat = new String[row][col];
			} else {
				System.out
						.println("Row or column specified do not match the required criteria");
				System.exit(1);
			}

			/**
			 * Read the contents of the file A look up is also created so that
			 * in the evaluation part computed values can be obtained easily and
			 * redundant computation is eliminated.
			 */
			char c = 'A';
			String x_buffer = null;

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if ((x_buffer = f.readLine()) != null) {
						mat[i][j] = x_buffer;
						lookup.put("" + c + (j + 1), x_buffer);
					} else {
						System.out
								.println("Data in the file dosent match the number of row and column");
						System.exit(1);
					}
				}
				c = (char) (c + 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/** Evaluate the expressions given in the spreadsheet */
		
		char c = 'A';
		System.out.println(col + " " + row);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				String address = "" + c + (j + 1);
				double result = eval(mat[i][j].split(" "), address);
				lookup.put(address, "" + result);
				String format = String.format("%.5f", result);
				System.out.println(format);
			}
			c = (char) (c + 1);
		}
	}

	/** Function that evaluates a given expression */
	
	static double eval(String[] s, String address) {
		ArrayDeque<String> sheet_value = new ArrayDeque<String>();
		ArrayDeque<Double> cell_value = new ArrayDeque<Double>();

		for (String token : s) {

			sheet_value.push(token);

			while (sheet_value.size() > 0) {
				token = sheet_value.pop();

				/** Check if this is a number*/
				if (isNumber(token)) {
					cell_value.push(Double.parseDouble(token));
				}

				/** Check for reference*/
				if (isReference(token)) {
					String[] internal_value = lookup.get(token).split(" ");
					for (String val : internal_value) {
						if (isNumber(val)) {
							cell_value.push(Double.parseDouble(val));
						} else {
							sheet_value.addLast(val);
						}
					}
					if (sheet_value.contains(address)) {
						System.out.println("Cyclic relationship");
						System.exit(1);
					}
				}

				/*Check if it is an operator*/
				if (isOperator(token)) {
					double val1 = cell_value.pop();
					double val2 = cell_value.pop();

					switch (token) {
					case "+":
						cell_value.push(val1 + val2);
						break;
					case "-":
						cell_value.push(val2 - val1);
						break;
					case "*":
						cell_value.push(val1 * val2);
						break;
					case "/":
                                if(val1 == 0.0){
							System.out.println("Divide by zero");
                                     System.exit(1);
						}
						else
						{
						  cell_value.push(val2 / val1);
						}
						break;
					default:
						System.out.println("Operator not supported");
						break;
					}
				}
			}
		}
		return cell_value.peek();
	}

	/** check if the token is a number */
	public static boolean isNumber(String token) {
		return (token.matches("-?[0-9]+"))
				|| (token.matches("-?[0-9]*[.]?[0-9]+"));
	}

	/** Check if the token is a reference */
	public static boolean isReference(String token) {
		return token.matches("^[A-Z].*");
	}

	/**
	 * Check if the token is an Operator I have not considered the increment and
	 * decrement operator
	 */
	public static boolean isOperator(String token) {
		return token.matches("[*-+/]");
	}
}