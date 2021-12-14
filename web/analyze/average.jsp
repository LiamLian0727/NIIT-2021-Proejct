<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="连仕杰">
    <title>Director View</title>

    <!-- custom css file link  -->
    <link rel="stylesheet" href="../css/styleA.css">

</head>
<body>

<!-- header section starts  -->
<!-- @author 袁蕾-->

<jsp:include page="navigationBar.jsp"></jsp:include>

<!-- header section ends -->
<div>
    <div class="divs" id="left" style="width:30%;float:left;">
        <form class="forms" action="../AverageVoteEcharts" method="post">
            <h1 class="h">Average</h1><br>
            <h2 class="h">typeKey: </h2><br>
            <select class="inputs" name="type">
                <option value="director">Director</option>
                <option value="writer">Writer</option>
                <option value="production_company">Production Company</option>
                <option value="actors">Actors</option>
            </select>
            <h2 class="h">Min:</h2><input class="inputs" type="number" name="min"/><br>
            <h2 class="h">Num:</h2><input class="inputs" type="number" name="num">
            <br><input type="submit" value="run"><br><br><br>
            <ul>
                <li>Type Key: Analysis of the category</li>
                <li>Min :Minimum number of works</li>
                <li>Num :number of table</li>
            </ul>
        </form>
    </div>
    <div class="divs" id="right" style="width:70%;float:left;">
        <div id="main" style="width: 100%;">
            <img src="../image/background4.jpg" style="width: 100%">
        </div>
    </div>
</div>


<!-- 图表 start -->
<script src="../js/echarts.min.js"></script>
<script type="text/javascript">
    let query = window.location.search.substring(1);
    let pair = query.split("=");
    if (pair[1] === 'success') {

        let acc = JSON.parse(JSON.stringify(${Ave}));
        let list = acc.rows;
        let data1 = [];
        let data2 = [];
        let dataAxis = [];
        let split;
        let option;

        for (let item of list) {
            dataAxis.push(item.key)
            split = item.value.split(":");
            data1.push(split[0])
            data2.push(split[1])
        }
        console.log(dataAxis);
        console.log(data1);
        console.log(data2);

        let dom = document.getElementById('main');
        let myChart = echarts.init(dom, null, {
            width: 700,
            height: 500
        });

        const colors = ['#5470C6', '#91CC75', '#EE6666'];
        option = {
            color: colors,
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                }
            },
            toolbox: {
                feature: {
                    dataView: { show: true, readOnly: false },
                    restore: { show: true },
                    saveAsImage: { show: true }
                }
            },
            legend: {
                data: ['Evaporation', 'Temperature']
            },
            xAxis: [
                {
                    type: 'category',
                    axisTick: {
                        alignWithLabel: true
                    },
                    data: dataAxis
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '评分',
                    min: function(value) {
                        return (value.min - 0.1);
                    },
                    max: function(value) {
                        return (value.max + 0.1);
                    },
                    position: 'right',
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: colors[0]
                        }
                    }
                },
                {
                    type: 'value',
                    name: '作品数量',
                    min: function(value) {
                        return (value.min - 1);
                    },
                    max: function(value) {
                        return (value.max + 1);
                    },
                    position: 'left',
                    axisLine: {
                        show: true,
                        lineStyle: {
                            color: colors[2]
                        }
                    }
                }
            ],
            series: [
                {
                    name: 'Evaporation',
                    type: 'bar',
                    data: data1
                },
                {
                    name: 'Temperature',
                    type: 'line',
                    yAxisIndex: 1,
                    data: data2
                }
            ]
        };

        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }

    }
    else if(pair[1] === 'error'){
        alert("error")
    }
</script>

