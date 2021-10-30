<!DOCTYPE html>
<html lang="ch">
<head>
    <meta charset="UTF-8">
    <title>Sign In</title>
    <style>
        input[type=text], input[type=password] {
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            border-radius: 400px;
            box-sizing: border-box;
        }

        input[type=button] {
            width: 40%;
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            margin: 25px 4%;
            border: none;
            border-radius: 400px;
            cursor: pointer;

        }

        input[type=button]:hover {
            background-color: #45a049;
        }

        div {
            width: 50%;
            position: absolute;
            left: calc(25%);
            border-radius: 40px;
            background-color: #f2f2f2;
            padding: 20px;
        }

    </style>
</head>

<script>
    function error(){
        var errorFromServlet ='<%=request.getParameter("error")%>';
        if(errorFromServlet=='yes'){
            alert("Password error!");
        }
    }
</script>

<body onload="error()">
<h1 style="text-align: center">Sign In</h1>
<div style="line-height: 30px;">
    <span style="font-family: verdana,serif; ">
        <form name="sign" action="" method="post">
            <title>Sign In</title>
            <br>User Name:       <br>
            <label><input type="text" name="UserName"></label>
            <br>Password:        <br>
            <label><input type="password" name="Password"></label>
            <br>
            <input type="button" value="Sign In" type="submit"
                   onclick="sign.action='SignIn';sign.submit();"/>
            <input type="button" value="Sign Up" type="submit"
                   onclick="sign.action='SignUp.jsp';sign.submit();"/>
        </form>
    </span>
</div>

</body>
</html>
