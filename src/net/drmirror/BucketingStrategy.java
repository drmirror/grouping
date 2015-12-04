package net.drmirror;

public interface BucketingStrategy<Item> {

	public Object bucketFor (Item i);
	
}
