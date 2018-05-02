(function(window,$){
    $.fn.extend({
        mySlide:function(arr,speed){
            var ele=$(this),myW=parseInt($(this).css("width")),myH=parseInt($(this).css("height")),arrlen=arr.length;
            //'<span class="back"><img src="img/back.png" alt="img"/></span>
            var html='<div class="sl_content">';
            if(arr[arrlen-1].imgurl!=undefined&&arr[arrlen-1].href!=undefined&&arr[arrlen-1].text!=undefined){
                html+='<a href="'+arr[arrlen-1].href+'"><img src="'+arr[arrlen-1].imgurl+'" alt="img"/><p>'+arr[arrlen-1].text+'</p></a>';
            }else if(arr[arrlen-1].imgurl!=undefined&&arr[arrlen-1].href!=undefined){
                html+='<a href="'+arr[arrlen-1].href+'"><img src="'+arr[arrlen-1].imgurl+'" alt="img"/></a>';
            }else{
                html+='<a href="javascript:void(0)"><img src="'+arr[arrlen-1].imgurl+'" alt="img"/></a>';
            }
            $.each(arr,function(n,obj){
                if(obj.imgurl!=undefined&&obj.href!=undefined&&obj.text!=undefined){
                    html+='<a href="'+obj.href+'"><img src="'+obj.imgurl+'" alt="img"/><p>'+obj.text+'</p></a>';
                }else if(obj.imgurl!=undefined&&obj.href!=undefined){
                    html+='<a href="'+obj.href+'"><img src="'+obj.imgurl+'" alt="img"/></a>';
                }else{
                    html+='<a href="javascript:void(0)"><img src="'+obj.imgurl+'" alt="img"/></a>';
                }
            });
            if(arr[0].imgurl!=undefined&&arr[0].href!=undefined&&arr[0].text!=undefined){
                html+='<a href="'+arr[0].href+'"><img src="'+arr[0].imgurl+'" alt="img"/><p>'+arr[0].text+'</p></a></div><ul>';
            }else if(arr[0].imgurl!=undefined&&arr[0].href!=undefined){
                html+='<a href="'+arr[0].href+'"><img src="'+arr[0].imgurl+'" alt="img"/></a></div><ul>';
            }else{
                html+='<a href="javascript:void(0)"><img src="'+arr[0].imgurl+'" alt="img"/></a></div><ul>';
            }
            $.each(arr,function(n,obj){
                html+='<li>'+(n+1)+'</li>'
            });
             html+='</ul>';
             //<span class="prev"><img src="img/prev.png" alt="img"/></span>
            ele.html(html);

            ele.css({
                "position":"relative",
                "overflow":"hidden"
            });
            ele.find(".sl_content").css({
                "width":myW*(arrlen+2)+"px",
                "height":myH+"px",
                "margin-left":-myW+"px"
            });
            ele.find(".sl_content>a").css({
                "text-decoration":"none",
                "position":"relative",
                "display":"block",
                "float":"left",
                "width":myW+"px",
                "height":myH+"px"
            });
            ele.find(".sl_content img").css({
                "display":"block",
                "width":myW+"px",
                "height":myH+"px"
            });
            ele.find("p").css({
                "position":"absolute",
                "width":"100%",
                "height":"15%",
                "background":"rgb(0,0,0)",
                "filter":"alpha(opacity=50)",
                "opacity":"0.5",
                "box-sizing":"border-box",
                // "text-align":"center",
                "margin":"0px",
                "color":"white",
                "z-index":"5",
                "left":"0px",
                "bottom":"0px",
                "padding-left":"15px"
            }).css("line-height",ele.find("p").css("height"));
            ele.find(".back").css({
                "cursor":"pointer",
                "display":"block",
                "position":"absolute",
                "filter":"alpha(opacity=70)",
                "opacity":"0.7",
                "width":"8%",
                "height":"100%",
                "z-index":"10",
                "left":"0px",
                "top":"0px"
            }).hover(function(){
                $(this).css({
                    "filter":"alpha(opacity=50)",
                    "opacity":"0.5"
                });
            },function(){
                $(this).css({
                    "filter":"alpha(opacity=70)",
                    "opacity":"0.7"
                });
            });
            ele.find(".prev").css({
                "cursor":"pointer",
                "display":"block",
                "position":"absolute",
                "filter":"alpha(opacity=70)",
                "opacity":"0.7",
                "width":"8%",
                "height":"100%",
                "z-index":"11",
                "right":"0px",
                "top":"0px"
            }).hover(function(){
                $(this).css("opacity","0.5");
            },function(){
                $(this).css("opacity","0.8");
            });
            ele.find("span>img").css({
                "position":"absolute",
                "width":"100%",
                "height":"auto",
                "z-index":"10",
                "left":"0px"
            }).css({
                "top":(parseInt( ele.find("span").css("height"))-parseInt( ele.find("span>img").css("height")))/2+"px"
            });
            ele.find("ul").css({
                "list-style":"none",
                "overflow":"hidden",
                "position":"absolute",
                "padding":"0px",
                "margin":"0px"
            });
            ele.find("ul li").css({
                "list-style":"none",
                "float":"left",
                "width":"18px",
                "height":"18px",
                "background":"rgb(200,200,200)",
                "filter":"alpha(opacity=70)",
                "opacity":"0.7",
                "border-radius":"50%",
                "text-align":"center",
                "line-height":"18px",
                "font-size":"10px",
                "color":"white",
                "margin":"3px",
                "cursor":"pointer"
            });
            ele.find("ul li:eq(0)").css({
                "background":"rgb(232,16,16)"
            });
            ele.find("ul").css({
                // "left":(myW-parseInt(ele.find("ul").css("width")))/2+"px",
                // "bottom":"16%"
                "right":"1%",
                "bottom":"1%",
                "z-index":"5"
            });

            var timer,myl=-myW;
            start();
            function start(){
                timer=setInterval(function(){
                    ele.find(".sl_content").stop().animate({"margin-left":myl+"px"},speed*0.2>300?300:speed*0.2,function(){
                        if(myl<=-(myW*(arrlen+1))){
                            ele.find(".sl_content").css("margin-left",-myW+"px");
                            myl=-myW;
                        }
                        var tli=(-myl/myW);
                        if(tli>=arrlen){
                            tli=arrlen;
                        }
                        ele.find("ul li").css({
                            "background":"rgb(200,200,200)"
                        });
                        ele.find("ul li:eq("+(tli-1)+")").css({
                            "background":"rgb(232,16,16)"
                        });
                        myl-=myW;
                    });
                },speed);
            }
            ele.hover(function(){
                clearInterval(timer);
                ele.find(".sl_content").stop();
            },function(){
                start();
            });
            ele.find(".prev").click(function(){
                myl=parseInt(ele.find(".sl_content").css("margin-left"));
                if(myl%myW!=0){
                    myl=-myW*Math.ceil((-myl)/myW);
                }else{
                    myl-=myW;
                }
                ele.find(".sl_content").stop().animate({"margin-left":myl+"px"},speed*0.2>300?300:speed*0.2,function(){
                    if(myl<=-(myW*(arrlen+1))){
                        ele.find(".sl_content").css("margin-left",-myW+"px");
                        myl=-myW;
                    }
                    var tli=(-myl/myW);
                    if(tli>=arrlen){
                        tli=arrlen;
                    }
                    ele.find("ul li").css({
                        "background":"rgb(200,200,200)"
                    });
                    ele.find("ul li:eq("+(tli-1)+")").css({
                        "background":"rgb(232,16,16)"
                    });
                    myl-=myW;
                });
            });
            ele.find(".back").click(function(){
                myl=parseInt(ele.find(".sl_content").css("margin-left"));
                if(myl%myW!=0){
                    myl=-myW*Math.floor((-myl)/myW);
                }else{
                    myl+=myW;
                }
                ele.find(".sl_content").stop().animate({"margin-left":myl+"px"},speed*0.2>300?300:speed*0.2,function(){
                    if(myl>=0){
                        ele.find(".sl_content").css("margin-left",-(myW*(arrlen))+"px");
                        myl=-(myW*(arrlen));
                    }
                    var tli=(-myl/myW);
                    if(tli>=arrlen){
                        tli=arrlen;
                    }
                    ele.find("ul li").css({
                        "background":"rgb(200,200,200)"
                    });
                    ele.find("ul li:eq("+(tli-1)+")").css({
                        "background":"rgb(232,16,16)"
                    });
                    myl-=myW;
                });
            });
            ele.find("ul li").click(function(){
                var myP=$(this).index()+1;
                myl=-myP*myW;
                ele.find(".sl_content").stop().animate({"margin-left":myl+"px"},speed*0.2>300?300:speed*0.2,function(){
                    var tli=(-myl/myW);
                    if(tli>=arrlen){
                        tli=arrlen;
                    }
                    ele.find("ul li").css({
                        "background":"rgb(200,200,200)"
                    });
                    ele.find("ul li:eq("+(tli-1)+")").css({
                        "background":"rgb(232,16,16)"
                    });
                    myl-=myW;
                });
            })
        }
    })
})(window,jQuery);