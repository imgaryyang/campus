function switchSchoolYear_(schoolYearId, deptId) {

    switchSchoolYear(schoolYearId, deptId, function () {
        refresh();
    });
}