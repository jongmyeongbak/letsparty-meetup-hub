let currentPage = 1;
let catNo;
let isChecked = true;
const urlParams = new URL(location.href).searchParams;
let value = urlParams.get('value');
console.log(value);

function selectCategory(cat) {
	//let btns = document.querySelectorAll(".cat-button button");
	let $btns = $(".cat-button button");
	$btns.each(function(i, btn) {
		btn.classList.remove("active");
	});
	$("#cat-" + cat).addClass("active");
	$("#party-list").empty();

	catNo = cat;
	value = " ";

	isChecked = true;
	currentPage = 1;
	getParties();
}

let target = document.querySelector("#target");

const option = {
	root: null,
	rootMargin: '0px 0px 0px 0px',
	threshole: 0.5
}

const onIntersect = (entries, observer) => {
	entries.forEach(entry => {
		if (entry.isIntersecting) {

			isChecked = true;
			getParties();
		}
	})
}
const observer = new IntersectionObserver(onIntersect, option);
observer.observe(target);

// ajax 쓰기
function getParties(pageNo) {
	if (isChecked) {
		$.ajax({
			url: "search-party",
			type: "GET",
			data: {
				pageNo: currentPage,
				catNo: catNo,
				value: value 
			},
			dataType: "json"
		}).done(function(response){
			console.log(response);
			if (response.length == 0) {
				isChecked = false;
			}
			//console.log(data);
			//console.log(arr);

			let htmlContent = "";

			response.forEach(item => {
				htmlContent += `
						<div class="party-item">
							<a href="/party/${item.no}">        
				            	<div class="img">
				                	<img src="${item.filename}" alt="">
				            	</div>
				            	<div class="content">
				                	<span class="title">${item.name}</span>
				                	<span class="description">${item.description}</span>
				                	<div class="category">
				                    	<span class="text">${item.category.name}</span>
				                	</div>
				                	<div class="info">
				                    	<span>${item.quota}/${item.curCnt}명</span>
				                    	<span>&#183</span>
				                    	<span>리더 ${item.leader.name}</span>
				                	</div>
				            	</div>
				        	</a>
				    	</div>
					`
			})
			document.querySelector("#party-list").innerHTML += htmlContent;
		}).fail(function(){
			console.log("error발생");
		})
	} else {
		document.querySelector("#party-list").innerHTML += htmlContent;
	}
	currentPage++;
}