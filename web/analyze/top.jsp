<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="刘宣兑">
    <title>Rating View</title>

    <!-- custom css file link  -->
    <link rel="stylesheet" href="../css/styleA.css">

</head>
<body>

<!-- header section starts  -->

<jsp:include page="navigationBar.jsp"></jsp:include>

<!-- header section ends -->

<!-- 图表 start -->
<body>

<div>
    <div class="divs" id="left" style="width:30%;float:left;">
        <form class="forms" action="../Top250Echarts" method="post">
            <h1 class="h">Top</h1><br>
            <h2 class="h">Min votes:</h2><input class="inputs" type="number" name="min"/><br>
            <h2 class="h">Num:</h2><input class="inputs" type="number" name="num">
            <br><input type="submit" value="run"><br><br><br>
            <ul>
                <li>Min votes: minimum votes required to be listed</li>
                <li>Num :number of top</li>
            </ul>
        </form>
    </div>
    <div class="divs" id="right" style="width:70%;float:left;">
        <div id="main" style="width: 100%;">
            <img src="../image/background3.jpg" style="width: 100%">
        </div>
    </div>
</div>

<script src="../js/echarts.min.js"></script>
<script type="text/javascript">
    let query = window.location.search.substring(1);
    let pair = query.split("=");
    if (pair[1] === 'success') {

        let acc = JSON.parse(JSON.stringify(${topN}));
        let list = acc.rows;
        let data = [];
        let dataAxis = [];

        for (let item of list) {
            dataAxis.push(item.key)
            data.push(item.value)
        }
        console.log(dataAxis);
        console.log(data);

        let dom = document.getElementById('main');
        let myChart = echarts.init(dom, null, {
            width: 700,
            height: 500
        });
        option = null;

        let dataShadow = [];

        for (let i = 0; i < data.length; i++) {
            dataShadow.push(5);
        }

        option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow',
                    label: {
                        show: true
                    }
                }
            },
            title: {
                text: 'TopN',
            },
            xAxis: {
                data: dataAxis,
                axisLabel: {
                    color: '#fff',
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                z: 10
            },
            yAxis: {
                min: function(value) {
                    return (value.min - 0.2);
                },
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    color: '#999'
                }
            },
            legend: {
                data: ['Growth', 'shuju'],
                itemGap: 5
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            series: [
                {
                    type: 'bar',
                    showBackground: true,
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#83bff6' },
                            { offset: 0.5, color: '#188df0' },
                            { offset: 1, color: '#188df0' }
                        ])
                    },
                    emphasis: {
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#2378f7' },
                                { offset: 0.7, color: '#2378f7' },
                                { offset: 1, color: '#83bff6' }
                            ])
                        }
                    },
                    data: data
                }
            ]
        };

        const zoomSize = 6;
        myChart.on('click', function (params) {
            console.log(dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)]);
            myChart.dispatchAction({
                type: 'dataZoom',
                startValue: dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)],
                endValue:
                    dataAxis[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
            });
        });

        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }

    }
    else if(pair[1] === 'error'){
        alert("error")
    }
</script>
</body>
