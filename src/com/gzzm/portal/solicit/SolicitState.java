package com.gzzm.portal.solicit;

/**
 * @author lk
 * @date 13-10-17
 */
public enum SolicitState
{
	notPublished("未发布"),
	published,
	stop;

	private String name;

	SolicitState()
	{
	}

	SolicitState(String name)
	{
		this.name = name;
	}

	public SolicitState setName(String name)
	{
		this.name = name;
		return this;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
