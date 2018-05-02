function selectDept()
{
    var deptId = Cyan.$("deptId").value;
    if (deptId != Cyan.Arachne.form.deptId)
    {
        Cyan.Arachne.form.deptId = deptId;
        reloadChildren(0);
    }
}