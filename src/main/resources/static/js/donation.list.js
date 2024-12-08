document.addEventListener("DOMContentLoaded", function () {
    // 초기 상태 설정
    const ongoingDonations = document.getElementById("ongoing-donations");
    const completedDonations = document.getElementById("completed-donations");
    const ongoingButton = document.getElementById("ongoingButton");
    const completedButton = document.getElementById("completedButton");
    const cards = document.querySelectorAll(".donation-card");

    // 기본 보이기 설정
    ongoingDonations.style.display = "block";
    completedDonations.style.display = "none";
    ongoingButton.classList.add("active");

    // 진행 중/전해진 버튼 클릭 이벤트
    ongoingButton.addEventListener("click", function () {
        ongoingDonations.style.display = "block";
        completedDonations.style.display = "none";
        this.classList.add("active");
        completedButton.classList.remove("active");
    });

    completedButton.addEventListener("click", function () {
        ongoingDonations.style.display = "none";
        completedDonations.style.display = "block";
        this.classList.add("active");
        ongoingButton.classList.remove("active");
    });

    // 카드 레이아웃 강제 설정
    cards.forEach((card) => {
        card.style.display = "flex";
        card.style.alignItems = "center";
        card.style.justifyContent = "space-between";
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

