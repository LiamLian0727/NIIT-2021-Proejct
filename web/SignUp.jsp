<!DOCTYPE html>
<html lang="ch">
<head>
    <meta charset="UTF-8">
    <title>Sign Up</title>
    <style>
        input[type=text], input[type=password], input[type=date], select{
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            border-radius: 400px;
            box-sizing: border-box;
        }

        input[type=submit] {
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            margin: 25px 0;
            border: none;
            border-radius: 400px;
            cursor: pointer;
        }

        input[type=submit]:hover {
            background-color: #45a049;
        }

        div {
            width: 50%;
            position:absolute;
            left:calc(25%);
            border-radius: 40px;
            background-color: #f2f2f2;
            padding: 20px;
        }

    </style>
</head>

<script>
    function error(){
        let errorFromServlet ='<%=request.getParameter("error")%>';
        if(errorFromServlet=='notEqual'){
            alert("Entering the password twice is different!");
        }else if (errorFromServlet=='hasExit'){
            alert('The user name already exists')
        }
    }
</script>

<body onload="error()">

<h1 style="text-align: center">Student Registration Form</h1>
<div style="line-height: 30px;">
    <span style="font-family: verdana,serif; ">
        <form action="SignUp" method="post">
            <title>Registration Form</title>
            <br>User Name:       <br>
            <label><input type="text" name="UserName"></label>
            <br>Password:        <br>
            <label><input type="password" name="Password"></label>
            <br>Insure Password: <br>
            <label><input type="password" name="PasswordAgain"></label>
            <br>EmailID:         <br>
            <label><input type="text" name="EmailID"></label>
            <br>MobileNo:        <br>
            <label><input type="text" name="MobileNo"></label> <br />Format:1234-56-67810
            <br>Majors:          <br>
            <label>
            <select name="Majors">
                <option value="default" disabled selected hidden>--majors--</option>
                <option value="BD"> Big Data </option>
                <option value="JAVA"> Java Programming </option>
            </select>
            </label>
            <br>Country:<br>
            <label>
                <select name="Country">
                    <option value="">China</option>
                    <option value="">India</option>
                    <option value="">Japan</option>
                    <option value="">America</option>
                </select>
            </label>
            <br><input type="submit" value="Sign up">
        </form>
    </span>
</div>
</body>


</html>