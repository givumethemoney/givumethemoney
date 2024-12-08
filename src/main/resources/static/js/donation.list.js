document.addEventListener("DOMContentLoaded", function () {
    // 1. 탭 전환 관련 설정
    const ongoingDonations = document.getElementById("ongoing-donations");
    const completedDonations = document.getElementById("completed-donations");
    const ongoingButton = document.getElementById("ongoingButton");
    const completedButton = document.getElementById("completedButton");

    // URL 파라미터에서 탭 확인
    const params = new URLSearchParams(window.location.search);
    const tab = params.get("tab");

    // 초기 탭 설정 함수
    function setActiveTab(tab) {
        if (tab === "completed") {
            ongoingDonations.classList.remove("active");
            completedDonations.classList.add("active");
            ongoingButton.classList.remove("active");
            completedButton.classList.add("active");
        } else {
            completedDonations.classList.remove("active");
            ongoingDonations.classList.add("active");
            completedButton.classList.remove("active");
            ongoingButton.classList.add("active");
        }
    }

    // 초기 활성 탭 설정
    setActiveTab(tab || "ongoing");

    // 버튼 클릭 이벤트
    ongoingButton.addEventListener("click", () => {
        window.location.search = "?tab=ongoing";
    });

    completedButton.addEventListener("click", () => {
        window.location.search = "?tab=completed";
    });
});