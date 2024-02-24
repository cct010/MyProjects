(function($){
	function crateCommentInfo(obj){

		if(typeof(obj.time) == "undefined" || obj.time == ""){
			obj.time = getNowDateFormat();
		}
		
		// var el = "<div class='comment-info'><header><img src='"+obj.img
		// +"'></header><div class='comment-right'><h3>"+obj.replyName+"</h3>"
		// +"<div class='comment-content-header'><span><i class='glyphicon glyphicon-time'></i>"
		// +obj.time+"</span>";
		var el = "<div class='comment-info'>"
		+"<div class='comment-right'><h3>"
		+"<img src='"+obj.img+"' class='commentimg2'></img>"
		+"<a class='commentUser' name='"  + obj.id
		+"'>" +obj.replyName + "</a>"
		//+"<span style='font-size: 16px; color: #aaa; margin-left :10px'>"+obj.time+"</span>"
		+"</h3>";
		

		//el = el+"<p class='content'>"+obj.content+"</p><div class='comment-content-footer'><div class='row'><div class='col-md-10'>";
		el = el+"<div class='content' style='width:780px;word-wrap:break-word;'>"+obj.content+"</div><div class='comment-content-footer'><div class='row'><div class='col-md-10'>";
		//评论内容
		// el = el+"</div><p class='content'>"+obj.content+"</p><div class='comment-content-footer'><div class='row'><div class='col-md-10'>";
		
		el = el  +
		"</div><div class='col-md-2'>" +
		"<span style='font-size: 16px; color: green; margin-left :70px'>" +obj.time+"</span>" +
		"<span class='reply-btn'>回复</span></div></div></div><div class='reply-list' id='"
		+obj.commentId +"'>";

		// var el = "<div class='articleList'><span class='imgc'><img src='" + obj.img
		// + "'  class='commentimg'></span><span><a  class='commentUser'> " +obj.replyName
		//  + "</a><div class='articleContent1' >"+obj.content ;
		

		if(obj.replyBody != "" && obj.replyBody.length > 0){
			var arr = obj.replyBody;
			console.log("replybody"+arr);
			for(var j=0;j<arr.length;j++){
				var replyObj = arr[j];
				el = el+createReplyComment(replyObj);
			}
		}

		// el = el + 	"</div><span style='color: #999;font-size: 14px;'>" + obj.time 
		// +"</span><span style='color: rgb(3, 23, 29);font-size: 14px;' class='reply-btn'> 回复</span></span></div>";
		el = el+"</div></div></div>";
		return el;
	}
	
	//返回每个回复体内容
	function createReplyComment(reply){
		
		var replyEl = "<div class='reply'><div><a href='javascript:void(0)' class='replyname' name='"
				+reply.id + "'>"+reply.replyName
				+"</a>: <a href='javascript:void(0)' >@"+reply.beReplyName
				+"</a><span>"+reply.content+"</span></div>"
				+ "<p><span>"+reply.time+"</span> <span class='reply-list-btn'>回复</span></p></div>";
		return replyEl;
	}
	function getNowDateFormat(){
		var nowDate = new Date();
		var year = nowDate.getFullYear();
		var month = filterNum(nowDate.getMonth()+1);
		var day = filterNum(nowDate.getDate());
		var hours = filterNum(nowDate.getHours());
		var min = filterNum(nowDate.getMinutes());
		var seconds = filterNum(nowDate.getSeconds());
		return year+"-"+month+"-"+day+" "+hours+":"+min+":"+seconds;
	}
	function filterNum(num){
		if(num < 10){
			return "0"+num;
		}else{
			return num;
		}
	}
	//返回每一个回复的必须信息,发送添加回复的请求
	function getCommentData(articleid,content,replyid,parentid){
		// console.log(blogId,userId,content,replyid,parentid);
		var commentdata = {
			articleid: articleid,
			content: content,
			replyid: replyid,
			parentid: parentid
		}
		// let strCommentData = JSON.stringify(commentdata);
		
		$.ajax({
			url: '/blog/comment/add',
			type: 'post' ,
			data: commentdata,
			// contentType: "application/json; charset=utf8",
			success: function (body) {
				if(body!=null && body.code==200 && body.data!=null){
					//location.href = location.href;
					//刷新评论
					RefreshComment();
				}else{
				    var text = body.msg;
                    //alert(text + "评论发表失败!");
					alert(text );
				}
				//console.log(body);
				
			}
		});
		
	}



	//点击回复按钮出现的,小文本输入框
	//一级评论的回复,二级评论的回复
	function replyClick(el){

		 let aText = "<div class='replybox'><textarea cols='100' rows='50' style='display: flex;' placeholder='来说几句吧......' class='mytextarea' ></textarea><span class='send'>发送</span></div>";
		 
		//  //一级评论的回复
		//  //获取当前登陆者的名字
		//  var replyusername = $(".commentimg")[0].name;
		//  console.log("当前登陆用户" + replyusername);

		//   //如果二级评论存在,说明是在回复二级评论,如果二级评论不存在,说明是在回复一级评论
		//   if(el.parent().parent().find("div").find("a:first").hasClass("replyname")){
		// 	//二级用户的名字
		// 	var bereply2 = el.parent().parent().find("div").find("a:first").text();
		// 	if(bereply2 === replyusername){
		// 		console.log("二级用户: "+ bereply2);
		// 		console.log("二级评论,你还是在回复自己哦!");
		// 	}
		//   }else{
		// 	//这是一级评论
 		// 	//一级评论的用户名字
 		// 	var bereply1 = el.parent().parent().parent().parent().find("h3").find("a").text();//被回复的用户
		// 	 if(bereply1 === replyusername){
		// 		console.log("一级用户: "+ bereply1);
		// 		console.log("一级评论,你还是在回复自己哦!");
		// 	}
		// }


		
		

		el.parent().parent().append(aText)
		.find(".send").click(function(){
			var content = $(this).prev().val();
			console.log("查看.send");
			console.log(el);
			console.log($(this));
			//判断是否只有空格,只有回车,把他们全都替换为"""
			var strValue=content; //回车
			//strValue = javaTrim(strValue);//空格
			if(content != "" && strValue!=""){
				var parentEl = $(this).parent().parent().parent().parent();
				console.log("找寻id");
				console.log( el.parent().parent().parent().parent().find(".reply-list")[0].id);
				var parentId= el.parent().parent().parent().parent().find(".reply-list")[0].id;//获取父Id
				console.log("commentId = "+ parentId);
				//获取当前登陆者的名字
				var replyusername = $(".commentimg")[0].name;
				console.log("当前登陆用户" + replyusername);
				var obj = new Object();
				obj.replyName= replyusername;
				if(el.parent().parent().hasClass("reply")){
					console.log("1111");
					obj.beReplyName = el.parent().parent().find("a:first").text();
					obj.respondentId = el.parent().parent().find("a:first")[0].name;
					console.log(el.parent().parent().find("a:first").text());
				}else{
					console.log("2222");
					//获取的是用户名字
					obj.beReplyName=parentEl.find("h3").find("a").text();
					obj.respondentId = parentEl.find("h3").find("a")[0].name;
					console.log(parentEl.find("h3").find("a").text());
					
				}
				obj.content=content;
				// obj.time = getNowDateFormat();
				console.log("回复数据的添加");
				console.log(obj);
				console.log(parentEl);
				// var articleid = location.search;
				// blogId = blogId.split("=")[1];
				// console.log(blogId);
				getCommentData(articleid,content,obj.respondentId,parentId);
				// var replyString = createReplyComment(obj);//创建回复div
				$(".replybox").remove();
				//加入回复列表,添加在最前面prepend,添加在最后append
				//parentEl.find(".reply-list").prepend(replyString).find(".reply-list-btn:first").click(function(){alert("不能回复自己");});
				
				
				
			}else{
				console.log("空内容2");
				//alert("文本为空!评论发表失败!");
				alert("文本为空!");
			}
			
		});


	}
	
	//点击评论
	$.fn.addCommentList=function(options){
		var defaults = {
			data:[],
			add:""
		}
		var option = $.extend(defaults, options);
		//加载数据,
		if(option.data.length > 0){
			var dataList = option.data;
			var totalString = "";
			//一开始页面加载的数据
			for(var i=0;i<dataList.length;i++){
				var obj = dataList[i];
				var objString = crateCommentInfo(obj);
				totalString = totalString+objString;
			}
			console.log("加载数据与回复22");
			
			//点击回复按钮,有一级评论回复按钮,二级回复评论按钮
			//一级评论的回复
			$(this).append(totalString).find(".reply-btn").click(function(){
				//点击输入框,发送按钮
				if($(this).parent().parent().find(".replybox").length > 0){
					$(".replybox").remove();
				}else{
					$(".replybox").remove();
					console.log("入口在哪里?");
					 //一级评论的回复
		 			//获取当前登陆者的名字
					var replyusername = $(".commentimg")[0].name;
					console.log("当前登陆用户" + replyusername);
					//这是一级评论
					//一级评论的用户名字
					var bereply1 = $(this).parent().parent().parent().parent().find("h3").find("a").text();//被回复的用户
					console.log("一级用户: "+ bereply1);
					//二级用户的id
					// console.log($(this).parent().parent().parent().parent().find("h3").find("a")[0].name);
					if(bereply1 === replyusername){
						console.log("一级评论,你还是在回复自己哦!");
						alert("不能回复自己!");
					}else{
						replyClick($(this));
					}	
				}
			});
			//二级评论的回复
			$(".reply-list-btn").click(function(){
				if($(this).parent().parent().find(".replybox").length > 0){
					$(".replybox").remove();
				}else{
					$(".replybox").remove();
	
					//获取当前登陆者的名字
					var replyusername = $(".commentimg")[0].name;
					console.log("当前登陆用户" + replyusername);
					//二级评论的回复按钮点击
					if($(this).parent().parent().find("div").find("a:first").hasClass("replyname")){
						//二级用户的名字
						var bereply2 = $(this).parent().parent().find("div").find("a:first").text();
						console.log("二级用户: "+ bereply2);
						// //二级用户的id
						// console.log($(this).parent().parent().find("div").find("a:first")[0].name);
						if(bereply2 === replyusername){
							console.log("二级评论,你还是在回复自己哦!");
							alert("不能回复自己!");
						}else{
							var commentdata = new Object();
							replyClick($(this));
						}
					}
					
				}
			})
		}
		
		//添加新数据
		//页面的评论框输入的内容,点击评论来到此
		//需要,判断空格,与回车
		//判断是否只有空格,只有回车,把他们全都替换为"""
		console.log("添加数据");
		console.log(option.add);
		var strValue= option.add.content;
		if(option.add != "" ){
			obj = option.add;
			var stra = crateCommentInfo(obj);
			console.log("添加新数据??"+obj);
			$(this).prepend(stra).find(".reply-btn").click(function(){
				replyClick($(this));
				if($(this).parent().parent().find(".replybox").length < 0){
					$(".replybox").remove();
				}else{
					$(".replybox").remove();
					replyClick($(this));
				}
			});
		}else {
			console.log("空内容1");
			// alert("文本为空!评论发表失败!");
		}
	}
	
	

	
})(jQuery);