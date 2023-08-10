   //   document.addEventListener('DOMContentLoaded', function() {
	   $(document).ready(function() {
		var Calendar = FullCalendar.Calendar;
		var Draggable = FullCalendar.Draggable;   
	   
		var containerEl = document.getElementById('external-events');
		var calendarEl = document.getElementById('calendar');
		var checkbox = document.getElementById('drop-remove');
		
		// 외부이벤트 초기화
		new Draggable(containerEl, {
		     itemSelector: '.fc-event',
		     eventData: function(eventEl) {
		        return {
		          title: eventEl.innerText
		        };
		     }
		});
		 var calendar = new Calendar(calendarEl, {
			  customButtons: {
				    myCustomButton: {
				      text: '일정 만들기',
				      click: function() {
				        $("#eventModal").modal("show");
				        
				        $("#addEvent").on("click", function(){
				        	var content = $("event_content").val();
				        	var start_date = $("event_start_date").val();
				        	var end_date = $("event_end_date").val();
				        	var end_time = $("event_start_time").val();
				        	var end_time = $("event_end_time").val();
				        	
				        if(content == null || content == ""){
				        	alert("내용을 입력하세요.");	
				        }else if(start_date == null || start_date == ""){
				        	alert("시작날짜를 입력하세요.");	
				        }else if(end_date == null || end_date == ""){
				        	alert("종료날짜를 입력하세요.");	
				        }else{
				        	var obj = {
				        		"title" : content,
				        		"start" : start_date,
				        		"end" : end_date,
				        		"start_time" : start_time,
				        		"end_time" : end_time
				        	}
				        	
				        	console.log(obj);
				        }
				        
				        })
				      }
				    }
				  },
		      // 원하는 지정 날짜에 일정등록하기
		      dateClick: function() {
		          text: '일정 만들기',

			        $("#eventModal").modal("show");
			        
			        $("#addEvent").on("click", function(){
			        	var content = $("event_content").val();
			        	var start_date = $("event_start_date").val();
			        	var end_date = $("event_end_date").val();
			        	var end_time = $("event_start_time").val();
			        	var end_time = $("event_end_time").val();
	    	   // alert('Clicked on: ' + info.dateStr);
	    	   // alert('Coordinates: ' + info.jsEvent.pageX + ',' + info.jsEvent.pageY);
	    	   // alert('Current view: ' + info.view.type);
	    	    // change the day's background color just for fun
	    	    //info.dayEl.style.backgroundColor = 'pink';
			        })
	    	  },
	    	  events: [
				{
					title: '종일 일정',
					start: '2023-08-10',
					allDay: true
				},
				{
					title: '2박 3일',
					start: '2023-08-17',
					end: '2023-08-19'
				},  
				{
					title: '오후 4시 미팅',
					start: '2023-08-10T16:00',
				},
				{
					title: '세미나',
					start: '2023-08-21',
					end: '2023-08-25'
				},
				{
					title: '데이트',
					start: '2023-08-29',
				},
				{
					title: '월말정산',
					start: '2023-08-31T15:00'
				}
			  ],
		      headerToolbar: {
		        left: 'prev,next,today',
		        center: 'title',
		        right:'myCustomButton'
		        //right: 'dayGridMonth,timeGridWeek,timeGridDay'
		      },
		      // 수정가능여부
		      editable: true,
		      // 드래그 허용
		      droppable: true, 
		      displayEventTime: false,
		      drop: function(info) {
		        // 드래그 후 제거 허용
		        if (checkbox.checked) {
		          // 드래그 가능한 이벤트를 목록에서 제거
		          info.draggedEl.parentNode.removeChild(info.draggedEl);
		      
		        }
		      },
		      
		      locale:"ko"
		    });

		    calendar.render();
		  });
	   
//        var calendar = new FullCalendar.Calendar(calendarEl, {
//          initialView: 'dayGridMonth',
//          locale:"ko"
//        });
//        calendar.render();
//      });
