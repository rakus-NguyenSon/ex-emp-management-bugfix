package jp.co.sample.emp_management.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private HttpSession session;

	@Autowired
	private EmployeeService employeeService;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(String searchName, Model model, Integer pageID) {

		List<Employee> employees = employeeService.showListByName(searchName);
		List<Integer> pageNumber = new ArrayList<>();
		List<Employee> employeesListForShow = new ArrayList<>();

		if (employees.isEmpty()) {
			model.addAttribute("SearchError", "１件もありませんでした");
			employees = employeeService.showList();
		}

		// 従業員一覧リストの長さによりページリストを作成
		for (Integer i = 1; i <= employees.size() / 10; i++) {
			pageNumber.add(i);
		}
		if (employees.size() % 10 != 0) {
			pageNumber.add(employees.size() / 10 + 1);
		}

		// 最初のアクセス対応
		if (pageID == null) {
			pageID = 1;
		}
		// ページIDにより10個の情報を取得
		for (Integer i = (pageID - 1) * 10; i < pageID * 10 && i < employees.size(); i++) {
			employeesListForShow.add(employees.get(i));
		}
		
		// データスコープの準備
		if(searchName==null) {
			searchName = "";
		}
		session.setAttribute("currentSearch", searchName);
		model.addAttribute("pages", pageNumber);
		model.addAttribute("employeeList", employeesListForShow);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

}
