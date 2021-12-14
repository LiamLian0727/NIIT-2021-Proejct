<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="author" content="郑欣然">
    <title>ECharts</title>

    <link rel="stylesheet" href="../css/styleA.css">
</head>
<body>
<!-- 为 ECharts 准备一个定义了宽高的 DOM -->
<jsp:include page="navigationBar.jsp"></jsp:include>

<div>
    <div class="divs" id="left" style="width:30%;float:left;">
        <form class="forms" action="../AccountEcharts" method="post">
            <h1 class="h">Account</h1><br>
            <h2 class="h">typeKey: </h2><br>
            <select class="inputs" name="type">
                <option value="language">Language</option>
                <option value="genre">Genre</option>
            </select>
            <h2 class="h">percentageMin:</h2><input class="inputs" type="text" name="min"
                                                    οnkeyup="this.value=this.value.replace(/[^\d.]/g,'')"/><br>
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
            <img src="../image/background1.jpg" style="width: 100%">
        </div>
    </div>
</div>

<script src="../js/echarts.min.js"></script>
<script type="text/javascript">

    // 基于准备好的dom，初始化echarts实例
    let query = window.location.search.substring(1);
    let pair = query.split("=");
    if (pair[1] === 'success') {

        let acc = JSON.parse(JSON.stringify(${Acc}));
        let list = acc.rows;

        let dataset = [];
        let region = [];

        for (let item of list) {
            region.push(item.key)
            dataset.push({value: item.value, name: item.key})
        }


        console.log(region); console.log(dataset);

        let dom = document.getElementById('main');
        let myChart = echarts.init(dom, null, {
            width: 700,
            height: 500
        });
        let app = {};
        let option = null;

        option = {
            title: {
                text: 'Percentage of films',
                x: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'right',
                data: region
            },
            series: [
                {
                    name: '访问来源',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '60%'],
                    data: dataset,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };

        app.currentIndex = -1;

        setInterval(function () {
            let dataLen = option.series[0].data.length;
            // 取消之前高亮的图形
            myChart.dispatchAction({
                type: 'downplay',
                seriesIndex: 0,
                dataIndex: app.currentIndex
            });
            app.currentIndex = (app.currentIndex + 1) % dataLen;
            // 高亮当前图形
            myChart.dispatchAction({
                type: 'highlight',
                seriesIndex: 0,
                dataIndex: app.currentIndex
            });
            // 显示 tooltip
            myChart.dispatchAction({
                type: 'showTip',
                seriesIndex: 0,
                dataIndex: app.currentIndex
            });
        }, 1000);

        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }
    } else if (pair[1] === 'error') {
        alert('error');
    }

</script>
</body>
</html>
