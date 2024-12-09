/*
	Telephasic by HTML5 UP
	html5up.net | @ajlkn
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/
let currentIndex = 0; // 현재 슬라이드 인덱스
const slides = document.querySelector('.slides'); // 슬라이드 요소

function showNextSlide() {
    currentIndex = (currentIndex + 1) % 3; // 3개의 이미지가 있으므로 인덱스를 순환
    const offset = -currentIndex * 100; // 이동할 오프셋 계산
    slides.style.transform = `translateX(${offset}%)`; // 슬라이드 이동
}

// 3초마다 다음 슬라이드를 보여주기
setInterval(showNextSlide, 3000);

(function($) {

	var	$window = $(window),
		$body = $('body');

	// Breakpoints.
		breakpoints({
			normal:    [ '1081px',  '1280px'  ],
			narrow:    [ '821px',   '1080px'  ],
			narrower:  [ '737px',   '820px'   ],
			mobile:    [ '481px',   '736px'   ],
			mobilep:   [ null,      '480px'   ]
		});

	// Play initial animations on page load.
		$window.on('load', function() {
			window.setTimeout(function() {
				$body.removeClass('is-preload');
			}, 100);
		});

		// 폰트 크기를 조정하는 함수
        function adjustFontSizeForElement(element, maxHeight) {
            let currentFontSize = parseFloat(window.getComputedStyle(element).fontSize); // 초기 폰트 크기
            while (element.scrollHeight > maxHeight && currentFontSize > 10) {
                currentFontSize -= 1; // 폰트 크기를 줄임
                element.style.fontSize = currentFontSize + "px"; // 업데이트된 폰트 크기를 적용
            }
        }

        // 모든 <p> 태그에 대해 폰트 크기를 조정
        function adjustFontSizeForAllParagraphs(parentElement, maxHeight) {
            const paragraphs = parentElement.querySelectorAll("p"); // 부모 안의 모든 <p> 태그를 선택
            paragraphs.forEach((p) => {
                adjustFontSizeForElement(p, maxHeight); // 각 <p> 태그의 폰트 크기 조정
            });
        }

        // DOM 로드 완료 후 실행
        document.addEventListener("DOMContentLoaded", () => {
            document.querySelectorAll(".donation-info").forEach((info) => {
                adjustFontSizeForAllParagraphs(info, 50); // .donation-info 안의 모든 <p> 태그 처리
            });
        });

});