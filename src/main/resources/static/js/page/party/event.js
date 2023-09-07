//$(function() {
  		//let clickedEvent;
  		// 부트스트랩의 모달객체 생성하기
  		//const eventModal = new bootstrap.Modal("#eventModal", {
		//	keyboard: false
		//  });
  		
  		// 모달 엘리먼트에 이벤트 핸들러 함수 등록하기
  		//let el = document.querySelector("eventModal");
  		
  		var calendar = null;
  		
  		function clearEventField() {
			  $(":input[name=title]").val("");
			  $(":input[name=description]").val("");
			  $(":input[name=startDate]").val("");
			  $(":input[name=endDate]").val("");
			  $(":input[name=startTime]").val("").prop("disabled", false);
			  $(":input[name=endTime]").val("").prop("disabled", false);
			  $(":checkbox[name=allDay]").prop("checked", false);
			  $(":input[name=place]").val("");
			  $(":input[name=noti]").val("");
		  };
  		
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
		
	  calendar = new Calendar(calendarEl, {
		  customButtons: {
			    myCustomButton: {
			      text: '일정 만들기',
			      click: function() {
			        $("#eventModal").modal("show");
			      }
			    }
			  },
	      // 원하는 지정 날짜에 일정등록하기
	      dateClick: function() {
		        $("#eventModal").modal("show");
		        
		        $("#save-event").on("click", function(){
		        	var title = $("title").val();
		        	var description = $("description").val();
		        	var startDate = $("startDate").val();
		        	var endDate = $("endDate").val();
		        	var startTime = $("startTime").val();
		        	var endTime = $("endTime").val();
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
	      
	      locale:'ko',
	    });

	    // 일정 클릭 시 팝업으로 일정 정보 표시
		calendar.setOption('eventClick', function(info) {
		  var startDate = moment(info.event.start).format('YYYY-MM-DD');
		  var endDate = moment(info.event.end).format('YYYY-MM-DD');
			
		  alert('일정 제목: ' + info.event.title + '\n시작: ' + startDate + '\n종료: ' + endDate);
		});
	    calendar.render();
	    
	                   
	    
	    $(":checkbox[name=allDay]").change(function() {
			if($(this).prop('checked')) {
				$(":input[name=startTime]").prop("readOnly",true);
				$(":input[name=endTime]").prop("readOnly",true);
			} else {
				let startTime = moment().format('HH:mm');
				let endTime = moment().add('1', 'h').format('HH:mm');
				$(":input[name=startTime]").prop("readOnly", false).val(startTime);
				$(":input[name=endTime]").prop("readOnly", false).val(endTime);
			}
		})
		
	    $("#save-event").click(function() {
			let eventData = {
			  title: $(":input[name=title]").val(),
			  description: $(":input[name=description]").val(),
			  startDate: $(":input[name=startDate]").val(),
			  endDate: $(":input[name=endDate]").val()
			};
			let allDay = $(":checkbox[name=allDay]:checked").val();
			if (allDay) {
				eventData['allDay'] = 0;
			} else {
				eventData['allDay'] = 1;
				eventData['startTime'] = $(":input[name=startTime]").val();
				eventData['endTime'] = $(":input[name=endTime]").val();
			}
			addEvent(eventData);
			$("#eventModal").modal("hide");
		});
		
		function addEvent(event){
			$.ajax({
				type: 'post',
				url: '/event/register',
				data: event,
				dataType: 'json'
			})
			.done(function(event) {
				console.log(event)
				calendar.addEvent(event);
			})
		}
	  });
	  
	//  function addSave () {
	//	  var allEvent = calendar.getEvents();
	//	  console.log(allEvent);
	// }
//        var calendar = new FullCalendar.Calendar(calendarEl, {
//          initialView: 'dayGridMonth',
//          locale:"ko"
//        });
//        calendar.render();
//      });
//});