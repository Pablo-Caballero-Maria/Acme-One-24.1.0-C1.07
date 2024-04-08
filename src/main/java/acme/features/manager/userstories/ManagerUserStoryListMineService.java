
package acme.features.manager.userstories;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.userstories.UserStory;
import acme.roles.Manager;

@Service
public class ManagerUserStoryListMineService extends AbstractService<Manager, UserStory> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerUserStoryRepository managerUserStoryRepository;


	// AbstractService interface ----------------------------------------------
	@Override
	public void authorise() {
		boolean status;
		int userStoryId;
		Manager manager;
		UserStory userStory;

		userStoryId = super.getRequest().getData("id", int.class);
		userStory = this.managerUserStoryRepository.findOneById(userStoryId);
		manager = userStory.getManager();

		status = userStory != null && super.getRequest().getPrincipal().hasRole(manager) && userStory.getManager().equals(manager);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<UserStory> objects;
		Principal principal;

		principal = super.getRequest().getPrincipal();
		objects = this.managerUserStoryRepository.findAllByManagerId(principal.getActiveRoleId());

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final UserStory object) {
		assert object != null;

		Manager manager;
		manager = object.getManager();

		Dataset dataset;
		dataset = super.unbind(object, "title", "description", "estimatedCost", "acceptanceCriteria", "priority", "link");
		dataset.put("manager", manager);
		super.getResponse().addData(dataset);
	}
}
