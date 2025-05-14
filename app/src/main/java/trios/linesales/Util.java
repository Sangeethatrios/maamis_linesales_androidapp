
package trios.linesales;

import java.util.Vector;


/**
 * The class ia a collection of tools for parsing String ,getting card type or
 * dealing with time and date
 * 
 * @author Nelson
 */
public class Util
{
	/**
	 * Returns the current HH:MM
	 * 
	 * @param time - time string
	 * @return format time string
	 */
	public static String getHHMM(String time)
	{
		String finalTime = "00/00 AM/PM";
		String hh = time.substring(0, 2);
		String mm = time.substring(3, 5);

		int newHH = Integer.parseInt(hh);
		int newMM = Integer.parseInt(mm);

		newMM = newMM % 60;

		if (newHH == 0)
		{
			finalTime = "12:" + newMM + " PM";
		} else
		{

			if (newHH > 12)
			{
				newHH = newHH % 12;
				finalTime = newHH + ":" + newMM + " PM";
			} else
				finalTime = newHH + ":" + newMM + " AM";
		}

		String HH = finalTime.substring(0, finalTime.indexOf(":"));
		String MM = finalTime.substring(finalTime.indexOf(":") + 1, finalTime
				.indexOf(" "));
		String AMPM = finalTime.substring(finalTime.indexOf(" "), finalTime
				.length());

		if (MM.length() == 1)
			MM = "0" + MM;

		finalTime = HH + ":" + MM /*+ " " */+ AMPM;

		return (finalTime);
	}

	/**
	 * This method returns the numeric value in string so that further it can be
	 * concanated with other value strings like day,hours,min,seconds at the
	 * time of getting eraseTime at savingTransaction.
	 * 
	 * @param month -
	 *            String values like "Jan","Feb"....etc.
	 * @return String
	 */
	private static String[] monthNames =
	{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec" };

	public static String getMonthDigit(String month)
	{
		if("123456789101112".indexOf(month)!=-1)
		{
			return month;
		}
		String mon = "-1";
		for (int i = 0; i < 12; i++)
		{
			if (equalsIgnoreCase(month, monthNames[i]))
			{
				mon = "" + (i + 1);
				break;
			}
		}
		return mon;
	}

	public static String getMonthName(int index)
	{
		if(index >= 1 && index <= 12)
		{
			return monthNames[index-1];
		}
		return "";
	}
	
	public static boolean equalsIgnoreCase(String str1, String str2)
	   {
		   if(str1 == null && str2 == null)
		   {
			   return true;
		   }
		   if((str1 == null && str2 != null)
				   || (str1 != null && str2 == null))
		   {
			   return false;
		   }
		   if(str1.toLowerCase().equals(str2.toLowerCase()))
		   {
			   return true;
		   }
		   else
		   {
			   return false;
		   }
	   }
	
	/**
    * trim the input string's space
    * 
    * @param oldString - input string
    * @return trimmed string
    */
	public static String trimSpace(String oldString)
	{
		if (null == oldString)
			return null;
		if (0 == oldString.length())
			return "";
		StringBuffer sbuf = new StringBuffer();
		int oldLen = oldString.length();
		for (int i = 0; i < oldLen; i++)
		{
			if (' ' != oldString.charAt(i))
				sbuf.append(oldString.charAt(i));
		}
		String returnString = sbuf.toString();
		sbuf = null;
		return returnString;
	}

	/**
	 * Convert hex string to byte array
	 * 
	 * @param s - input String
	 * @param offset - start position
	 * @param len - byte length
	 * @return byte array
	 */
	public static byte[] hex2byte(String s, int offset, int len)
	{
		byte[] d = new byte[len];
		int byteLen = len * 2;
		for (int i = 0; i < byteLen; i++)
		{
			int shift = (i % 2 == 1) ? 0 : 4;
			d[i >> 1] |= Character.digit(s.charAt(offset + i), 16) << shift;
		}
		return d;
	}

	/**
	 * Convert hex string to byte array
	 * 
	 * @param s - input String
	 * @return byte array
	 */
	public static byte[] hexString2bytes(String s)
	{
		if (null == s)
			return null;
		s = trimSpace(s);
		return hex2byte(s, 0, s.length() >> 1);
	}









	/**
	 * Seperate String with str token
	 * 
	 * @param str - the string which will be cut
	 * @return cut string array
	 */
	@SuppressWarnings("unused")
	private static String[] seperateStr(String str)
	{
		boolean doubleSpace = false;
		int wordCount = 0;
		StringBuffer sb = new StringBuffer();
		if (str == null || str.length() == 0)
		{
			return null;
		}
		for (int j = 0; j < str.length(); j++)
		{
			if (str.charAt(j) == ' ')
			{
				if (!doubleSpace)
					wordCount++;

				doubleSpace = true;
				continue;
			}
			doubleSpace = false;
		}
		String st[] = new String[wordCount + 1];
		int i = 0;

		doubleSpace = false;
		String ch = "";
		for (int j = 0; j < str.length(); j++)
		{
			if (str.charAt(j) == ' ')
			{
				if (!doubleSpace)
				{
					st[i] = sb.toString();
					sb.delete(0, sb.length());
					i++;
				}
				doubleSpace = true;
				continue;
			} else
			{
				sb.append(str.charAt(j));
			}
			doubleSpace = false;
		}

		st[i] = sb.toString();
		;
		return st;
	}

	/**
	 * Fit the original string to the page
	 * 
	 * @param matter - input data
	 * @param lineSize - display width
	 * @param isBeginWithSpace - true if begin with space- 
	 * @return display string
	 */
	public static String fitToThePage(String matter,int lineSize,boolean isBeginWithSpace)  
	  {
	      if(matter.equals(""))
	          return ""; 
	      
	      String space=" ";
	      String bSpace="";
	      if(isBeginWithSpace)
	          bSpace =" ";
	      
	       boolean doubleSpace = false;
	      
	        int j=0;       
	        int word=1;    
	        
	        // This loop will find that how much words are present in the string
	        for(j=0;j<matter.length();j++)
	        {
	            if(matter.charAt(j)==' ')
	            {
	                if(!doubleSpace)
	                    word++;

	                doubleSpace = true;
	                continue;
	            }
	            doubleSpace = false;
	        }
	        String st[] = new String[word];
	        String ch ="";
	        int i=0;  
	        
	        doubleSpace = false;
	        //This loop will store words in the String array st[]
	        for(j=0;j<matter.length();j++)
	        {
	            if(matter.charAt(j)==' ')
	            {
	                if(!doubleSpace)
	                {
	                    st[i] = ch;
	                    ch="";   
	                    i++;     
	                }
	                doubleSpace = true;
	                continue;
	            }
	            else
	            {
	                ch = ch + matter.charAt(j);
	            }
	          doubleSpace = false;
	        }
	        st[i]=ch;
	        
	        ch = "";
	        String newString="";
	        @SuppressWarnings("unused")
			int len = st.length;
	        
	        for(i=0 ; i<word ; i++)
	        {
	                ch = ch + " "+ st[i];
	                
	                if(!isBeginWithSpace)
	                    ch = ch.trim();
	                if( ch.length() > lineSize )
	                {
	                        newString = newString +"\n" +bSpace + st[i];
	                        ch = "";
	                        ch = bSpace + st[i];
	                }else
	                {
	                        newString = newString + space + st[i];
	                        if(!isBeginWithSpace)
	                            newString = newString.trim();
	                }
	        }
	        return newString;
	  }

	

	
	/** 
	 * This method splits date in the String array and return it .
	 * @param date - date in the string format
	 * @return String[]
	 */
	public static String[] filterDate(String date)
	{
		return seperateStr(date);
	}

	/**
	 * This method align two string left & right respactively in given char
	 * size.
	 * 
	 * @param param1
	 *            string-1
	 * @param param2
	 *            string-2
	 * @param cpl
	 *            number of characters
	 * @return formated Strings
	 */

	public static String nameLeftValueRightJustify1(String param1, String param2,String param3,String param4,
													int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return leftJustify(param1,5)+rightJustify(param2,6)+rightJustify(param3,6)+rightJustify(param4,7);
	}
	public static String nameLeftValueRightJustify(String param1, String param2,String param3,
													int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return rightJustify(param1,12)+rightJustify(param2,8)+rightJustify(param3,12);
	}

	public static String nameLeftValueRightJustifybottomsales(String param1, String param2,String param3,
												   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,12)+leftJustify(param2,8)+rightJustify(param3,12);
	}
	public static String nameLeftValueRightJustifysalesbill(String param1, String param2,String param3,
															  int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,16)+leftJustify(param2,18)+rightJustify(param3,8);
	}

	public static String nameLeftValueRightJustifysalessummary(String param1, String param2,String param3,
															int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,16)+leftJustify(param2,15)+rightJustify(param3,10);
	}

	public static String nameLeftValueRightJustifynotreceived(String param1, String param2,String param3,
															   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,10)+leftJustify(param2,21)+rightJustify(param3,10);
	}

    public static String nameLeftValueRightJustify(String param1, String param2,String param3,String param4,
                                                   int cpl) {
        if(param1 == null)
            param1 = "";
        if(param2 == null)
            param2 = "";
        if(param3 == null)
            param3 = "";
        if(param4 == null)
            param4 = "";

        return rightJustify(param1,7)+ rightJustify(param2,2) +rightJustify(param3,3)+rightJustify(param4,21);
    }

	public static String nameLeftValueRightJustifytaxprint(String param1, String param2,String param3,String param4,
												   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,4)+ rightJustify(param2,10) +rightJustify(param3,12)+rightJustify(param4,12);
	}

	public static String nameLeftValueRightJustify5(String param1, String param2,String param3,String param4,
													String param5,String param6,
												   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";
        if(param5 == null)
            param5 = "";

		if(param6 == null)
			param6 = "";

		return rightJustify(param1,6)+ rightJustify(param2,6)
				+rightJustify(param3,6) +rightJustify(param4,6)+rightJustify(param5,6)
				+rightJustify(param6,10);
	}
	public static String nameLeftValueRightJustifysalesitem(String param1, String param2,String param3,String param4,
													int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,5)+ " "+ leftJustify(param2,6) +rightJustify(param3,8) +rightJustify(param4,12);
	}
	public static String nameLeftValueRightJustifysalesitem1(String param1, String param2,String param3,String param4,String param5,
															int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";
		if(param5 == null)
			param5 = "";

		return rightJustify(param1,2)+ " "+ leftJustify(param2,4) +rightJustify(param3,6)+ " " +rightJustify(param4,7) +rightJustify(param5,10);
	}
	public static String nameLeftValueRightJustifycash(String param1, String param2,String param3,
															int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return rightJustify(param1,10)+ " "+ rightJustify(param2,4)  +rightJustify(param3,24);
	}
	public static String nameLeftValueRightJustifycashvalues(String param1, String param2,String param3, String param4,
													   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,9)+ " "+ rightJustify(param2,10) + " "+ rightJustify(param3,9)
				+ " "+ rightJustify(param4,10) ;
	}
	public static String nameLeftValueRightJustifycashtaxvalues(String param1, String param2,String param3, String param4,
															 int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,7)+ " "+ rightJustify(param2,12) + " "+ rightJustify(param3,9)
				+ " "+ rightJustify(param4,10) ;
	}
	public static String nameLeftValueRightJustifysalestaxvalue(String param1, String param2,String param3,String param4,
															int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,5)+ " "+ rightJustify(param2,13) +rightJustify(param3,7) +rightJustify(param4,12);
	}
	public static String nameLeftValueRightJustify2(String param1, String param2,String param3,String param4,
													int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";


		return leftJustify(param1,9)+rightJustify(param2, 9)+rightJustify(param3,9)+rightJustify(param4, 3);
	}
	public static String nameLeftValueRightJustify3(String param1, String param2,String param3,String param4,
													int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";


		return leftJustify(param1,10)+rightJustify(param2, 10)+rightJustify(param3,12)+leftJustify(param4, 12);
	}
	public static String nameLeftValueRightJustify_cashrpt(String param1, String param2,String param3,
														   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,16)+rightJustify(param2,3)+rightJustify(param3,12);
	}
	public static String nameLeftValueRightJustify(String param1, String param2,
			int cpl) {
		 if(param1 == null)
         	param1 = "";
         if(param2 == null)
         	param2 = "";

		return param1+rightJustify(param2, (cpl - param1.length()));
	}
	public static String nameLeftValueRightJustify1(String param1, String param2,
												   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";

		return param1+rightJustify(param2, 9);
	}
	public static String nameLeftValueRightJustify_tot(String param1, String param2,String param3,String param4,
													   int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";

		return rightJustify(param1,7)+ rightJustify(param2,2) +rightJustify(param3,7)+rightJustify(param4,18);
	}
	public static String nameLeftValueLeftJustify(String param1 ,
												   int cpl) {
		if(param1 == null)
			param1 = "";


		return leftJustify(param1,cpl);
	}

	/**
	 * Right align to the string in given number of chars
	 * 
	 * @param item
	 *            String to be aligned
	 * @param digits
	 *            number of characters
	 * @return formated strings
	 */
	public static String rightJustify(String item, int digits) {
		StringBuffer buf = null;
		if(digits < 0)
		{
			buf = new StringBuffer();
		}
		else
		{
			buf = new StringBuffer(digits);
		}
		for (int i = 0; i < digits - item.length(); i++) {
			buf.append(" ");
		}
		buf.append(item);
		return buf.toString();
	}
	
	public static String leftJustify(String item, int digits) {
		StringBuffer buf = null;
		
		if(digits < 0)
		{
			buf = new StringBuffer();
		}
		else
		{
			buf = new StringBuffer(digits);
		}

		buf.append(item);
		for (int i = 0; i < digits - item.length(); i++) {
			buf.append(" ");
		}

		return buf.toString();
	}
	/**
	 * this method gives only last 4 digit of card number preceding with *.
	 * 
	 * @param ccNum
	 *            card number
	 * @return string
	 */
	public static String getAcountAsterixData(String ccNum) 
	{
		if(ccNum.length() < 4)
			return ccNum;
		int len = ccNum.length();
		String temp = "";
		for (int i = 0; i < (len - 4); i++) {
			temp = temp + "*";
		}
		return (temp + ccNum.substring((len - 4), (len)));
	}
	
	
	/**
	 * Center allign the string in specified digits
	 * 
	 * @param item
	 *            String to be allign
	 * @param digits
	 *            number of characters
	 * @return formated string
	 */
	public static String center(String item, int digits) {
		StringBuffer buf = null;
		if(digits < 0)
		{
			buf = new StringBuffer();
		}
		else
		{
			buf = new StringBuffer(digits);
		}
		int len = item.length();
		for (int i = 0; i < (digits - len) / 2; i++) {
			buf.append(" ");
		}
		buf.append(item);
		for (int i = 0; i < (digits - len) / 2; i++) {
			buf.append(" ");
		}
		return buf.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector processWrappedText(String text, int width)
	{
		Vector v = new Vector();
		if (text == null)
			return v;
		int cursor = 0;

		
		// needed to be modified  (xsx) 
		while (cursor < text.length())
		{
			int remainderLength = text.length() - cursor;
			int increment = 1;
			while ( (increment < remainderLength) &&
					(increment <= width))
			{
				increment++;
			}
			
			String subString = text.substring(cursor, cursor + increment);
			if ((subString.charAt(increment - 1) == ' ') ||
				(cursor + increment == text.length()) ||
				(text.charAt(cursor + increment) == ' '))
			{
				// no need to find last space
			}
			else
			{
				// need to backtrack to last space
				int lastSpaceIndex = subString.lastIndexOf(' ');
				if (lastSpaceIndex > 0)
				{
					increment = lastSpaceIndex;
				}
			}
			
			v.addElement(text.substring(cursor, cursor + increment));
			
			cursor += increment;
		}
		
		return v;
	}
	public static String nameLeftValueRightJustifybottomsalesv1(String param1, String param2,String param3,
															  int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,16)+leftJustify(param2,3)+rightJustify(param3,13);
	}
	public static String nameLeftValueRightJustifybottomsalesv2(String param1, String param2,String param3,
															  int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";

		return leftJustify(param1,8)+leftJustify(param2,12)+rightJustify(param3,12);
	}

	public static String nameLeftValueRightJustifysalesamountsummary(String param1, String param2,String param3,String param4,
																	 int cpl) {
		if(param1 == null)
			param1 = "";
		if(param2 == null)
			param2 = "";
		if(param3 == null)
			param3 = "";
		if(param4 == null)
			param4 = "";
		return leftJustify(param1,12)+leftJustify(param2,9)+leftJustify(param3,9)+rightJustify(param4,10);
	}
}
