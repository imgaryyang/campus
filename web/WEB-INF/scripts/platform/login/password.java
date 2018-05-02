             public String checkPassword(com.gzzm.platform.login.UserOnlineInfo user, String password) throws Exception
             {
                 if (password.length() < 8)
                 {
                     return "密码长度最少要8位";
                 }
                 else
                 {
                     boolean number = false;
                     boolean lowerCase = false;
                     boolean upperCase = false;
                     boolean other = false;
                     int length = password.length();

                     for (int i = 0; i < length; i++)
                     {
                         char c = password.charAt(i);

                         if (c >= '0' && c <= '9')
                             number = true;
                         else if (c >= 'a' && c <= 'z')
                             lowerCase = true;
                         else if (c >= 'A' && c <= 'Z')
                             upperCase = true;
                         else
                             other = true;
                     }

                     int n = 0;
                     if (number)
                         n++;
                     if (lowerCase)
                         n++;
                     if (upperCase)
                         n++;
                     if (other)
                         n++;

                     if (n < 3)
                         return "大写字母、小写字母、数字和特殊符号中至少要包含三种";
                     else
                         return null;
                 }
             }