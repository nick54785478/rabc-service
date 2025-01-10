package com.example.demo.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.demo.domain.group.aggregate.GroupInfo;
import com.example.demo.domain.group.aggregate.entity.GroupRole;
import com.example.demo.domain.group.command.CreateGroupCommand;
import com.example.demo.domain.group.command.CreateOrUpdateGroupCommand;
import com.example.demo.domain.role.aggregate.RoleInfo;
import com.example.demo.domain.share.GroupCreated;
import com.example.demo.domain.share.GroupInfoQueried;
import com.example.demo.domain.share.GroupOptionQueried;
import com.example.demo.domain.share.GroupRoleQueried;
import com.example.demo.domain.share.enums.YesNo;
import com.example.demo.exception.ValidationException;
import com.example.demo.infra.repository.GroupInfoRepository;
import com.example.demo.infra.repository.RoleInfoRepository;
import com.example.demo.util.BaseDataTransformer;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupService {

	private GroupInfoRepository groupInfoRepository;
	private RoleInfoRepository roleInfoRepository;

	/**
	 * 建立一筆群組資料
	 * 
	 * @param command
	 * @return GroupCreated
	 */
	public GroupCreated create(CreateGroupCommand command) {
		GroupInfo group = new GroupInfo();
		group.create(command);
		GroupInfo saved = groupInfoRepository.save(group);
		return BaseDataTransformer.transformData(saved, GroupCreated.class);
	}

	/**
	 * 建立多筆群組資訊(僅限於前端使用 Inline-Edit)
	 * 
	 * @param command
	 */
	public void createOrUpdate(List<CreateOrUpdateGroupCommand> commands) {

		// 取得 id 清單
		List<Long> ids = commands.stream().filter(command -> command.getId() != null)
				.map(CreateOrUpdateGroupCommand::getId).collect(Collectors.toList());

		// 取出清單相對應資料
		List<GroupInfo> roles = groupInfoRepository.findByIdIn(ids);

		Map<Long, GroupInfo> map = roles.stream().collect(Collectors.toMap(GroupInfo::getId, Function.identity()));

		List<GroupInfo> groupList = commands.stream().map(command -> {
			// 修改
			if (!Objects.isNull(command.getId()) && !Objects.isNull(map.get(command.getId()))) {
				GroupInfo group = map.get(command.getId());
				group.update(command);
				return group;
			} else {
				// 新增
				GroupInfo group = new GroupInfo();
				group.create(command);
				return group;
			}
		}).collect(Collectors.toList());

		groupInfoRepository.saveAll(groupList);
	}

	/**
	 * 查詢符合條件的群組資料
	 * 
	 * @param type
	 * @param name
	 * @param activeFlag
	 * @param numberOfRows 資料筆數
	 * @param pageNumber   頁碼
	 * @return List<GroupInfoQueried>
	 */
	@Transactional
	public Page<GroupInfoQueried> query(String type, String name, String activeFlag, Integer numberOfRows,
			Integer pageNumber) {
		Page<GroupInfo> groups = groupInfoRepository.findAllWithSpecification(type, name, activeFlag, numberOfRows,
				pageNumber);
		return BaseDataTransformer.transformData(groups, GroupInfoQueried.class);
	}

	/**
	 * 查詢符合條件的群組資料
	 * 
	 * @param id
	 * @return GroupInfoQueried
	 */
	@Transactional
	public GroupInfoQueried query(Long id) {
		Optional<GroupInfo> opt = groupInfoRepository.findById(id);
		if (opt.isPresent()) {
			GroupInfo group = opt.get();
			GroupInfoQueried groupQueried = BaseDataTransformer.transformData(group, GroupInfoQueried.class);
			// 取得 Role Id 清單
			List<Long> roleIds = group.getRoles().stream().filter(e -> Objects.equals(e.getActiveFlag(), YesNo.Y))
					.map(GroupRole::getRoleId).collect(Collectors.toList());

			List<RoleInfo> roles = roleInfoRepository.findByIdInAndActiveFlag(roleIds, YesNo.Y);

			List<GroupRoleQueried> groupRoles = BaseDataTransformer.transformData(roles, GroupRoleQueried.class);
			groupQueried.setRoles(groupRoles);
			return groupQueried;

		} else {
			throw new ValidationException("VALIDATION_FAILED", "該群組 ID 有誤，查詢失敗");
		}
	}

	/**
	 * 查詢群組角色
	 * 
	 * @param groupId
	 * @return GroupRolesQueried
	 */
	@Transactional
	public List<GroupRoleQueried> queryRoles(Long groupId) {
		Optional<GroupInfo> opt = groupInfoRepository.findById(groupId);
		if (opt.isEmpty()) {
			return new ArrayList<>();
		} else {
			GroupInfo group = opt.get();
			List<Long> roleIds = group.getRoles().stream().map(GroupRole::getRoleId).collect(Collectors.toList());
			List<GroupRoleQueried> result = roleInfoRepository.findByIdIn(roleIds).stream().map(role -> {
				GroupRoleQueried groupRoleQueried = new GroupRoleQueried();
				groupRoleQueried.setId(role.getId());
				groupRoleQueried.setName(role.getName());
				groupRoleQueried.setCode(role.getCode());
				groupRoleQueried.setDescription(role.getDescription());
				return groupRoleQueried;
			}).collect(Collectors.toList());
			return result;
		}
	}

	/**
	 * 刪除多筆角色資料
	 * 
	 * @param ids 要被刪除的 id 清單
	 */
	public void delete(List<Long> ids) {
		List<GroupInfo> groups = groupInfoRepository.findByIdInAndActiveFlag(ids, YesNo.Y);
		groups.stream().forEach(group -> {
			group.delete();
		});
		groupInfoRepository.saveAll(groups);
	}

	/**
	 * 查詢群組下拉選單資訊
	 * 
	 * @param str 群組資訊字串
	 * @return List<GroupOptionQueried>
	 */
	public List<GroupOptionQueried> getGroupInfoOtions(String str) {
		return BaseDataTransformer.transformData(groupInfoRepository.findAllWithSpecification(str),
				GroupOptionQueried.class);
	}
}
