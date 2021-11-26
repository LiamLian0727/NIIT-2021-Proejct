<%@ page import="static utils.Filters.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>ECharts</title>
    <!-- 引入刚刚下载的 ECharts 文件 -->

</head>
<body>
<p>Here is a table</p>
<input class="check" id="director" type="checkbox" value="导演"/><label for="director">导演</label><br/>
<input class="check" id="actors" type="checkbox" value="演员"/><label for="actors">演员</label><br/>
<input type="button" onclick="get()">
<div id="main" style="width:600px;height:400px;" onclick="get()"></div>
<script type="text/javascript">

    <%!
    String[] value = new String[3];
    String[] type = {"director","actors"};
    int choice = 0;
    %>

    function init(){
        <%inits();%>
        alert("Success");
    }

    function get() {
        let elementsByClassName = document.getElementsByClassName("check");
        let valueCheck = elementsByClassName[0].value;
        if (valueCheck === "actors") {
            <% choice = 1;%>
        }

        <%
        for(int i = 0; i < value.length; i++) {
          value[i] = filterScan(type[choice])[i];
        }
        %>

        let myChart = echarts.init(document.getElementById('main'));

        let option = {
            tooltip: {
                trigger: 'axis',
                formatter: '{a}:{b}:{c}',
                axisPointer: {
                    type: 'cross',
                    crossStyle: {
                        color: '#888'
                    }
                }
            },
            legend: {
                show: true,
                bottom: 0,                 //legend位置的调整
                left: 10,
                textStyle: {
                    color: '#5e859e',
                    fontSize: 10
                },
                itemGap: 20,
                data: ['平均评分'],
                inactiveColor: '#ccc'
            },
            //图位置调整
            grid: {
                top: '5%',
                left: '0%',
                right: '0%',
                bottom: '10%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    name: '评分',
                    data: [<%= value[0]%>],
                    axisLine: {
                        lineStyle: {
                            color: '#5e859e',   //横坐标轴和字体颜色
                            width: 2            //这里是为了突出显示加上的
                        }
                    },
                    axisPointer: {
                        type: 'shadow'

                    },
                    axisTick: {
                        show: true,
                        interval: 0
                    }
                },

            ],
            //设置两个y轴，左边显示数量，右边显示概率
            yAxis: [
                {
                    type: 'value',
                    name: '评分',
                    show: true,
                    interval: 100,
                    axisLine: {
                        lineStyle: {
                            color: '#5e859e',
                            width: 2
                        }
                    }
                },
                {
                    type: 'value',
                    name: '作品数量',
                    min: 0,
                    max: 100,
                    interval: 10,
                    axisLine: {
                        lineStyle: {
                            color: '#5e859e',//纵坐标轴和字体颜色
                            width: 2
                        }
                    }
                }
            ],
            //每个设备分数量、概率2个指标，只要让他们的name一致，即可通过，legeng进行统一的切换
            series: [
                {
                    name: '平均评分',
                    type: 'bar',
                    color: '#f17e0e',
                    data: [<%= value[1]%>],
                    barWidth: '50%'

                },
                {
                    name: '平均评分',
                    type: 'line',
                    color: '#2f87f1',
                    yAxisIndex: 1,    //这里要设置哪个y轴，默认是最左边的是0，然后1，2顺序来。
                    data: [<%= value[2]%>],
                    label: {
                        show: true,
                        formatter: function (data) {
                            return data.value;
                        }
                    },
                    symbolSize: 10
                }
            ]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }

</script>

<script src="js/echarts.min.js"></script>
</body>
</html>
