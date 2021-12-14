<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="袁蕾">
    <title>Country View</title>

    <!-- custom css file link  -->
    <link rel="stylesheet" href="../css/styleA.css">

</head>
<body>

<!-- header section starts  -->
<!-- @author 袁蕾-->

<jsp:include page="navigationBar.jsp"></jsp:include>

<!-- header section ends -->

<!-- 图表 start -->
<div>
    <div class="divs" id="left" style="width:30%;float:left;">
        <form class="forms" action="../SumIncomeEcharts" method="post">
            <h1 class="h">Account</h1><br>
            <h2 class="h">Income Type: </h2><br>
            <select class="inputs" name="type1">
                <option value="usa_gross_income">Usa Gross Income</option>
                <option value="worlwide_gross_income">Worlwide Gross Income</option>
            </select>
            <h2 class="h">Key Type: </h2><br>
            <select class="inputs" name="type2">
                <option value="country">Country</option>
                <option value="production_company">Production Company</option>
                <option value="original_title">Movie</option>
            </select>
            <h2 class="h">Num:</h2><input class="inputs" type="number" name="num">
            <br><input type="submit" value="run"><br><br><br>
            <ul>
                <li>Type Key: Analysis of the category</li>
                <li>Percentage Min :Minimum Percentage of works</li>
            </ul>
        </form>
    </div>
    <div class="divs" id="right" style="width:70%;float:left;">
        <div id="main" style="width: 100%;">
            <img src="../image/background.jpg" style="width: 100%">
        </div>
    </div>
</div>

<script src="../js/echarts.min.js"></script>
<script type="text/javascript">

    // 基于准备好的dom，初始化echarts实例
    let query = window.location.search.substring(1);
    let pair = query.split("=");
    if (pair[1] === 'success') {

        let acc = JSON.parse(JSON.stringify(${Sum}));
        let list = acc.rows;

        let data1 = [];
        let data2 = [];
        let region = [];
        let split;

        for (let item of list) {
            region.push(item.key)
            split = item.value.split(":");
            data1.push(split[0])
            data2.push(split[1])
        }


        console.log(region);
        console.log(data1);
        console.log(data2);

        let dom = document.getElementById('main');
        let myChart = echarts.init(dom, null, {
            width: 700,
            height: 500
        });
        let option;

        option = {
            title: {
                text: 'Income or Sum'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {},
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                boundaryGap: [0, 0.01],
                axisLabel: {
                    formatter: (value) => {
                        value = (value / 1000) + 'K';
                        return value;
                    }
                }
            },
            yAxis: {
                type: 'category',
                data: region
            },
            series: [
                {
                    name: 'Income',
                    type: 'bar',
                    data: data1
                },
                {
                    name: 'Budget',
                    type: 'bar',
                    data: data2
                }
            ]
        };

        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }

    } else if (pair[1] === 'error') {
        alert('error');
    }

</script>
</body>
</html>
